package com.example.warehouse.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "article")
@Data
public class ContainedArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonProperty("art_id")
    private Long inventoryId;

    @JsonProperty("amount_of")
    private int quantity;
}
