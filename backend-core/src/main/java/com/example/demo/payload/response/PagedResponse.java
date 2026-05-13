package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Generic paged response wrapper
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PagedResponse<T> of(Page<T> page) {
        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    public static <T> PagedResponse<T> of(List<T> content, Pageable pageable, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        int currentPage = pageable.getPageNumber();
        return PagedResponse.<T>builder()
                .content(content)
                .page(currentPage)
                .size(pageable.getPageSize())
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(currentPage == 0)
                .last(currentPage >= totalPages - 1)
                .hasNext(currentPage < totalPages - 1)
                .hasPrevious(currentPage > 0)
                .build();
    }
}
