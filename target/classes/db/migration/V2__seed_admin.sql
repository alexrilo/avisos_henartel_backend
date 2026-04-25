-- V2__seed_admin.sql
-- Seed initial roles and admin user for ServiFlow

-- Insert roles
INSERT INTO roles (nombre, descripcion) VALUES 
    ('ADMINISTRADOR', 'Usuario con acceso completo al sistema'),
    ('COORDINADOR', 'Usuario que gestiona avisos y asigna técnicos'),
    ('TECNICO', 'Usuario que ejecuta trabajos en campo')
ON CONFLICT (nombre) DO NOTHING;

-- Insert default admin user
-- Password: admin123 (BCrypt hash - $2b$10$HCNIeDjfSb6MrpyrVnE1DeAibjRD6znlWVGBiiEXuGxPCFW/9pml6)
INSERT INTO usuarios (username, password, nombre, apellido, email, rol_id, activo, created_at)
SELECT 
    'admin',
    '$2b$10$HCNIeDjfSb6MrpyrVnE1DeAibjRD6znlWVGBiiEXuGxPCFW/9pml6',
    'Administrador',
    'Sistema',
    'admin@serviflow.com',
    r.id,
    true,
    CURRENT_TIMESTAMP
FROM roles r
WHERE r.nombre = 'ADMINISTRADOR'
ON CONFLICT (username) DO NOTHING;

-- Verify admin was created
SELECT id, username, email, nombre, apellido, rol_id, activo 
FROM usuarios 
WHERE username = 'admin';