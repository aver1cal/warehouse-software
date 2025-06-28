package com.example.warehouse.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @JsonProperty("art_id")
    private Long id;

    private String name;

    @JsonProperty("stock")
    private int stock;

    public Inventory(Inventory item) {
        this.id = item.id;
        this.name = item.name;
        this.stock = item.stock;
    }
}
