-- Flyway migration: V3__create_clientes_table.sql
-- Creates the clientes table for ServiFlow MVP

CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('PARTICULAR', 'EMPRESA')),
    nombre_o_razon_social VARCHAR(200) NOT NULL,
    telefono VARCHAR(50) NOT NULL UNIQUE,
    persona_contacto VARCHAR(200),
    observaciones VARCHAR(1000),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP
);

-- Indexes for efficient querying
CREATE INDEX idx_clientes_nombre ON clientes(nombre_o_razon_social);
CREATE INDEX idx_clientes_telefono ON clientes(telefono);
CREATE INDEX idx_clientes_estado ON clientes(estado);
CREATE INDEX idx_clientes_tipo ON clientes(tipo);
