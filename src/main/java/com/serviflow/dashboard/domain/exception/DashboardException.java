package com.serviflow.dashboard.domain.exception;

import com.serviflow.shared.domain.exception.DomainException;

/**
 * Base exception for dashboard domain errors.
 * All dashboard-specific exceptions should extend this class.
 */
public class DashboardException extends DomainException {

    public DashboardException(String message) {
        super(message);
    }

    public DashboardException(String message, Throwable cause) {
        super(message, cause);
    }
}