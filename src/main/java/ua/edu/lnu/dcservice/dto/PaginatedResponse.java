package ua.edu.lnu.dcservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedResponse<T> {

    private final long total;
    private final long page;

    private final List<T> data;
}
