-- Ejecutar conectado a postgres como administrador, fuera de una transacción.
-- Reemplazar la contraseña antes de ejecutar.
CREATE ROLE sigpi LOGIN PASSWORD 'CAMBIAR_PASSWORD_LOCAL';
CREATE DATABASE sigpi_alicorp OWNER sigpi ENCODING 'UTF8';
