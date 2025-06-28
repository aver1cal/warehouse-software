package com.example.warehouse.backend.service.inventory;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.repository.InventoryRepository;

@Service
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<Inventory> getAllItems() {
        return inventoryRepository.findAll();
    }

    public Inventory getItemById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    public void updateStock(Collection<Inventory> items) {
        inventoryRepository.saveAll(items);
    }
    
}
