package com.example.warehouse.backend.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderedProduct {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    @NotNull(message = "Quantity is required")
    private int quantity;
}
