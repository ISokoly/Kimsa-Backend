package proyecto.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proyecto.dto.order.*;
import proyecto.model.*;
import proyecto.repo.*;
import proyecto.service.interfaces.InterOrderService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements InterOrderService {

    private static final String GENERIC_DNI = "00000001";

    private final OrderRepo orderRepo;
    private final OrderDetailRepo detailRepo;
    private final ProductRepo productRepo;
    private final EntityManager em;

    private final DiscountService discountService;
    private final TableRepo tableRepo;

    @Override
    public List<OrderResponseDTO> getAll() {
        return orderRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public OrderResponseDTO getById(Integer id) {
        Order o = orderRepo.findById(id).orElse(null);
        if (o == null) return null;
        return toResponse(o);
    }

    @Transactional
    @Override
    public OrderResponseDTO create(OrderCreateDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty())
            throw new IllegalArgumentException("El pedido debe tener al menos un ítem.");

        final LocalDate today = LocalDate.now();

        Order o = new Order();

        // 🔹 Obtenemos el cliente completo (para saber si es genérico y su fecha de nacimiento)
        Client client = em.getReference(Client.class, dto.getIdClient());
        o.setClient(client);

        o.setUser(em.getReference(User.class, dto.getIdUser()));
        o.setOrderDate(today);
        o.setOrderTime(LocalTime.now());
        o.setStatus(Order.OrderStatus.Pending);

        final boolean isDelivery = Boolean.TRUE.equals(dto.getDelivery());
        o.setDelivery(isDelivery);

        if (isDelivery) {
            o.setTable(null);
        } else {
            RestaurantTable mesa = requireUsableTable(dto.getIdTable(), null);
            o.setTable(mesa);
        }

        o = orderRepo.save(o);

        if (!isDelivery && dto.getIdTable() != null) {
            final Integer idMesa = dto.getIdTable();
            tableRepo.findById(idMesa).ifPresent(t -> {
                t.setActive(true);
                tableRepo.save(t);
            });
        }

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDTO item : dto.getItems()) {
            Product p = productRepo.findById(item.getIdProduct())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getIdProduct()));

            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;

            BigDecimal baseUnit = BigDecimal.valueOf(p.getPrice());

            // 🎂 Descuento considerando cumpleaños (cliente NO genérico)
            int pct = getApplicableDiscountPct(p.getIdProduct(), today, client);

            BigDecimal unitWithDiscount = baseUnit
                    .multiply(BigDecimal.ONE.subtract(
                            BigDecimal.valueOf(pct).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal sub = unitWithDiscount
                    .multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderDetail d = new OrderDetail();
            d.setOrder(o);
            d.setProduct(p);
            d.setQuantity(quantity);
            d.setSubtotal(sub);

            detailRepo.save(d);

            total = total.add(sub);
        }

        o.setTotal(total);
        return toResponse(orderRepo.save(o));
    }

    @Transactional
    @Override
    public OrderResponseDTO update(Integer id, OrderUpdateDTO dto) {
        Order o = orderRepo.findById(id).orElse(null);
        if (o == null) return null;

        Integer prevTableId = (o.getTable() != null) ? o.getTable().getIdTable() : null;

        if (dto.getIdClient() != null) {
            o.setClient(em.getReference(Client.class, dto.getIdClient()));
        }
        if (dto.getIdUser() != null) {
            o.setUser(em.getReference(User.class, dto.getIdUser()));
        }

        if (dto.getDelivery() != null || dto.getIdTable() != null) {
            if (Boolean.TRUE.equals(dto.getDelivery())) {
                if (prevTableId != null) {
                    tableRepo.findById(prevTableId).ifPresent(t -> {
                        t.setActive(false);
                        tableRepo.save(t);
                    });
                }
                o.setDelivery(true);
                o.setTable(null);
            } else {
                o.setDelivery(false);
                if (dto.getIdTable() != null) {
                    Integer newTableId = dto.getIdTable();
                    RestaurantTable nuevaMesa = requireUsableTable(newTableId, prevTableId);

                    if (prevTableId != null && !prevTableId.equals(newTableId)) {
                        tableRepo.findById(prevTableId).ifPresent(t -> {
                            t.setActive(false);
                            tableRepo.save(t);
                        });
                    }

                    o.setTable(nuevaMesa);
                    nuevaMesa.setActive(true);
                    tableRepo.save(nuevaMesa);
                }
            }
        }

        if (dto.getStatus() != null) {
            o.setStatus(dto.getStatus());

            if (dto.getStatus() == Order.OrderStatus.Confirmed ||
                    dto.getStatus() == Order.OrderStatus.Cancelled) {
                Integer tableId = (o.getTable() != null) ? o.getTable().getIdTable() : null;
                if (tableId != null) {
                    tableRepo.findById(tableId).ifPresent(t -> {
                        t.setActive(false);
                        tableRepo.save(t);
                    });
                }
            }
        }

        if (dto.getItems() != null) {
            LocalDate refDate = (o.getOrderDate() != null) ? o.getOrderDate() : LocalDate.now();

            // Cliente que queda finalmente en la orden (puede ser actualizado arriba)
            Client client = o.getClient();

            List<OrderDetail> current = o.getDetails();
            if (current != null && !current.isEmpty()) {
                detailRepo.deleteAll(current);
                current.clear();
            }

            BigDecimal total = BigDecimal.ZERO;

            for (OrderItemDTO item : dto.getItems()) {
                Product p = productRepo.findById(item.getIdProduct())
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + item.getIdProduct()));

                int quantity = (item.getQuantity() != null) ? item.getQuantity() : 1;

                BigDecimal baseUnit = BigDecimal.valueOf(p.getPrice());

                // 🎂 Descuento considerando cumpleaños (cliente NO genérico)
                int pct = getApplicableDiscountPct(p.getIdProduct(), refDate, client);

                BigDecimal unitWithDiscount = baseUnit
                        .multiply(BigDecimal.ONE.subtract(
                                BigDecimal.valueOf(pct).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                        .setScale(2, RoundingMode.HALF_UP);

                BigDecimal sub = unitWithDiscount
                        .multiply(BigDecimal.valueOf(quantity))
                        .setScale(2, RoundingMode.HALF_UP);

                OrderDetail d = new OrderDetail();
                d.setOrder(o);
                d.setProduct(p);
                d.setQuantity(quantity);
                d.setSubtotal(sub);
                detailRepo.save(d);

                total = total.add(sub);
            }

            o.setTotal(total);
        } else {
            o.recomputeTotal();
        }

        return toResponse(orderRepo.save(o));
    }

    @Override
    public List<OrderResponseDTO> getConfirmed() {
        return orderRepo.findByStatus(Order.OrderStatus.Confirmed)
                .stream().map(this::toResponse).toList();
    }

    /* ==================== Helpers ==================== */

    private RestaurantTable requireUsableTable(Integer tableId, Integer allowedBusyIdIfSame) {
        if (tableId == null) {
            throw new RuntimeException("Debe seleccionar una mesa o marcar delivery.");
        }

        RestaurantTable t = tableRepo.findById(tableId)
                .orElseThrow(() -> new RuntimeException("La mesa seleccionada no existe."));

        if (t.isDisabled()) {
            throw new RuntimeException("La mesa seleccionada está deshabilitada.");
        }

        boolean isSameAsAllowed = (allowedBusyIdIfSame != null && allowedBusyIdIfSame.equals(tableId));
        if (t.isActive() && !isSameAsAllowed) {
            throw new RuntimeException("La mesa seleccionada ya está ocupada.");
        }

        return t;
    }

    /**
     * 🎂 Si es cliente NO genérico y hoy es su cumpleaños → 50% fijo.
     * En caso contrario, usa la lógica normal de descuentos por día y producto.
     */
    private int getApplicableDiscountPct(Integer idProduct, LocalDate date, Client client) {
        if (isBirthdayNonGeneric(client, date)) {
            return 50;
        }
        // Lógica normal que ya tenías
        return getApplicableDiscountPct(idProduct, date);
    }

    /**
     * Lógica original de descuentos por producto/día (sin cumpleaños).
     */
    private int getApplicableDiscountPct(Integer idProduct, LocalDate date) {
        if (idProduct == null) return 0;

        List<Discount> all = discountService.getDiscountsByProduct(idProduct);
        if (all == null || all.isEmpty()) return 0;

        Discount.DayWeek todayDw = mapDayOfWeek(date.getDayOfWeek());

        List<Discount> active = all.stream()
                .filter(d -> !d.isDisabled())
                .toList();

        if (active.isEmpty()) return 0;

        int maxDay = active.stream()
                .filter(d -> d.getTypeDay() != null
                        && d.getTypeDay() != Discount.DayWeek.General
                        && d.getTypeDay() == todayDw)
                .map(Discount::getPercentage)
                .max(Comparator.naturalOrder())
                .orElse(0);

        if (maxDay > 0) return maxDay;

        return active.stream()
                .filter(d -> d.getTypeDay() == Discount.DayWeek.General)
                .map(Discount::getPercentage)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    /**
     * Devuelve true si el cliente NO es genérico y la fecha coincide con su cumpleaños (mes/día).
     */
    private boolean isBirthdayNonGeneric(Client c, LocalDate date) {
        if (c == null) return false;

        // Genérico: no aplica promo cumpleaños
        if (c.getDni() != null && GENERIC_DNI.equals(c.getDni())) {
            return false;
        }

        LocalDate birth = c.getBirthdate();
        if (birth == null) return false;

        return birth.getMonthValue() == date.getMonthValue()
                && birth.getDayOfMonth() == date.getDayOfMonth();
    }

    private Discount.DayWeek mapDayOfWeek(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> Discount.DayWeek.Lunes;
            case TUESDAY -> Discount.DayWeek.Martes;
            case WEDNESDAY -> Discount.DayWeek.Miercoles;
            case THURSDAY -> Discount.DayWeek.Jueves;
            case FRIDAY -> Discount.DayWeek.Viernes;
            case SATURDAY -> Discount.DayWeek.Sabado;
            case SUNDAY -> Discount.DayWeek.Domingo;
        };
    }

    private OrderResponseDTO toResponse(Order o) {
        OrderResponseDTO r = new OrderResponseDTO();
        r.setIdOrder(o.getIdOrder());
        r.setIdClient(o.getClient() != null ? o.getClient().getIdClient() : null);
        r.setIdUser(o.getUser() != null ? o.getUser().getIdUser() : null);
        r.setIdTable(o.getTable() != null ? o.getTable().getIdTable() : null);
        r.setOrderDate(o.getOrderDate());
        r.setOrderTime(o.getOrderTime());
        r.setStatus(o.getStatus());
        r.setTotal(o.getTotal());
        r.setDelivery(o.isDelivery());

        List<OrderDetailDTO> details = o.getDetails().stream().map(d -> {
            OrderDetailDTO dd = new OrderDetailDTO();
            dd.setIdDetail(d.getIdDetail());
            dd.setIdOrder(o.getIdOrder());
            dd.setIdProduct(d.getProduct() != null ? d.getProduct().getIdProduct() : null);
            dd.setQuantity(d.getQuantity());
            dd.setSubtotal(d.getSubtotal());
            return dd;
        }).toList();
        r.setDetails(details);
        return r;
    }
}
