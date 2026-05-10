-- Seeds para entorno de desarrollo (sintaxis MySQL/MariaDB).
-- INSERT IGNORE evita el error si las filas ya existen (la BD persiste entre arranques).

INSERT IGNORE INTO rol (id, nombre, descripcion, activo, created_at, updated_at)
VALUES (1, 'ADMIN', 'Administrador del sistema', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT IGNORE INTO rol (id, nombre, descripcion, activo, created_at, updated_at)
VALUES (2, 'USER', 'Usuario regular', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
