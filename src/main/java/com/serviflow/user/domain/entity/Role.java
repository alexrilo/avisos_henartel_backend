package com.serviflow.user.domain.entity;

/**
 * Enum representing user roles in ServiFlow.
 * 
 * ADMINISTRADOR - Full system access
 * COORDINADOR - Manages avisos and assigns technicians
 * TECNICO - Executes jobs in the field
 */
public enum Role {
    ADMINISTRADOR,
    COORDINADOR,
    TECNICO
}
