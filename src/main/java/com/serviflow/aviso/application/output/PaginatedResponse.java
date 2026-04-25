package com.serviflow.aviso.application.output;

import java.util.List;

/**
 * Generic paginated response record.
 * 
 * @param <T> the content type
 * @param content the list of items for the current page
 * @param totalElements total number of elements across all pages
 * @param totalPages total number of pages
 * @param currentPage the current page number (0-based)
 * @param pageSize the page size
 */
public record PaginatedResponse<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize
) {}