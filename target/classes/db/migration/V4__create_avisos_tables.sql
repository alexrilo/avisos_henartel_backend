-- V4: Create avisos, observaciones, and correlativos tables
-- ServiFlow MVP - Avisos feature

-- Avisos table
CREATE TABLE avisos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    numero_correlativo VARCHAR(20) NOT NULL UNIQUE,
    descripcion VARCHAR(2000) NOT NULL,
    prioridad VARCHAR(20) NOT NULL CHECK (prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'URGENTE')),
    estado VARCHAR(30) NOT NULL CHECK (estado IN ('NUEVO', 'ASIGNADO', 'EN_CURSO', 'COMPLETADO', 'CANCELADO', 'PENDIENTE_SEGUIMIENTO')),
    calle VARCHAR(200) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    localidad VARCHAR(100) NOT NULL,
    provincia VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(10) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_programada TIMESTAMP,
    tecnico_id BIGINT,
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP
);

-- Observaciones table
CREATE TABLE observaciones (
    id BIGSERIAL PRIMARY KEY,
    aviso_id BIGINT NOT NULL REFERENCES avisos(id) ON DELETE CASCADE,
    contenido VARCHAR(1000) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    usuario VARCHAR(100) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Correlativos counter table
CREATE TABLE aviso_correlativos (
    year INTEGER PRIMARY KEY,
    last_sequence INTEGER NOT NULL DEFAULT 0
);

-- Indexes for performance
CREATE INDEX idx_avisos_cliente ON avisos(cliente_id);
CREATE INDEX idx_avisos_estado ON avisos(estado);
CREATE INDEX idx_avisos_prioridad ON avisos(prioridad);
CREATE INDEX idx_avisos_tecnico ON avisos(tecnico_id);
CREATE INDEX idx_avisos_fecha_creacion ON avisos(fecha_creacion);
CREATE INDEX idx_observaciones_aviso ON observaciones(aviso_id);