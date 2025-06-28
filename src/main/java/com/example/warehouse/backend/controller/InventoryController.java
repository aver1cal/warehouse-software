package com.example.warehouse.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.service.inventory.InventoryService;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    
    private final InventoryService orderService;

    public InventoryController(InventoryService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getInventory() {
        List<Inventory> response = orderService.getAllItems();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}