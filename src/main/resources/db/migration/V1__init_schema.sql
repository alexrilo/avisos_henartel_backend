-- V1__init_schema.sql
-- ServiFlow Database Schema - Phase 0
-- Only creates tables for implemented features (Auth + Users)
-- Future features (clientes, avisos, trabajos) will have their own migrations.

-- ENUMS
CREATE TYPE rol_nombre AS ENUM ('ADMINISTRADOR', 'COORDINADOR', 'TECNICO');

-- ROLES TABLE
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    nombre rol_nombre NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- USUARIOS TABLE
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    rol_id BIGINT NOT NULL REFERENCES roles(id),
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT REFERENCES usuarios(id)
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_rol ON usuarios(rol_id);
CREATE INDEX idx_usuarios_activo ON usuarios(activo);

-- Trigger for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_usuarios_updated_at
    BEFORE UPDATE ON usuarios
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
