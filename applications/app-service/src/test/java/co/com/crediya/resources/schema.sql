DROP TABLE IF EXISTS usuario;

CREATE TABLE usuario (
  id_usuario SERIAL PRIMARY KEY,
  nombre VARCHAR(100),
  apellido VARCHAR(100),
  email VARCHAR(100) UNIQUE,
  documento_identidad VARCHAR(50),
  telefono VARCHAR(50),
  id_rol INT,
  salario_base BIGINT,
  fecha_nacimiento DATE,
  direccion VARCHAR(255)
);
