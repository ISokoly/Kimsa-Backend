-- Usuario por defecto (admin/password)
INSERT INTO users (name, last_name, dni, number_phone, direction, rol, username, password, disabled, administrator_permissions)
VALUES (
  'Administrador',
  'Default',
  '00000000',
  '000000000',
  'Dirección por defecto',
  'Administrator',
  'admin',
  'password',
  false,
  true
);

INSERT INTO users (name, last_name, dni, number_phone, direction, rol, username, password, disabled, administrator_permissions)
VALUES (
  'Employee',
  'Default',
  '00000001',
  '000000000',
  'Dirección por defecto',
  'Employee',
  'employee',
  'password',
  false,
  false
);

INSERT INTO client (name, dni, birthdate)
VALUES (
  'Generico',
  '00000001',
  '2000-01-01'
);