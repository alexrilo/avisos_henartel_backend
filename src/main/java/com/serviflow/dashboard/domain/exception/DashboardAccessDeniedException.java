package com.serviflow.dashboard.domain.exception;

import com.serviflow.shared.domain.exception.DomainException;

/**
 * Exception thrown when a user without sufficient privileges tries to access dashboard.
 * Returns HTTP 403 Forbidden.
 */
public class DashboardAccessDeniedException extends DomainException {

    private static final String DEFAULT_MESSAGE = "Access denied: dashboard is available only for ADMIN and COORDINADOR roles";

    public DashboardAccessDeniedException() {
        super(DEFAULT_MESSAGE);
    }

    public DashboardAccessDeniedException(String message) {
        super(message);
    }
}