package com.example.warehouse.backend.api.v1;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.time.LocalDate;

import com.example.warehouse.backend.model.OrderedProduct;

import lombok.Data;

@Data
public class OrderRequest {
    
    @NotNull(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Order date is required")
    private LocalDate orderDate;

    @NotEmpty(message = "At least one product is required")
    private List<OrderedProduct> products;
}
