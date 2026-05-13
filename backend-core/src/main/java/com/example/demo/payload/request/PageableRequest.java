package com.example.demo.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Pageable request wrapper with validation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableRequest {
    
    @Min(value = 0, message = "Page must be >= 0")
    @Builder.Default
    private int page = 0;
    
    @Min(value = 1, message = "Size must be >= 1")
    @Max(value = 100, message = "Size must be <= 100")
    @Builder.Default
    private int size = 20;
    
    @Builder.Default
    private String sortBy = "id";
    
    @Builder.Default
    private String sortDir = "asc";

    public Pageable toPageable() {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    public Pageable toPageable(Sort.Direction defaultDirection, String defaultSort) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = defaultSort;
        }
        if (sortDir == null || sortDir.isBlank()) {
            sortDir = "asc";
        }
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    public Pageable toPageableNoSort() {
        return PageRequest.of(page, size);
    }
}
