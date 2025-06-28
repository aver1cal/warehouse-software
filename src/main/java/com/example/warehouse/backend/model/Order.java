package com.example.warehouse.backend.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private LocalDate orderDate;
    private OrderStatus status;

    public Order(String customerName, LocalDate orderDate, OrderStatus status) {
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.status = status;
    }

    public enum OrderStatus {
        PENDING,
        PROCESSED,
        CANCELLED,
        OUT_OF_STOCK
    }
}