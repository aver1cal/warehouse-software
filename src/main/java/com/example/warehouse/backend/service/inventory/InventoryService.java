package com.example.warehouse.backend.service.inventory;

import java.util.Collection;
import java.util.List;

import com.example.warehouse.backend.model.Inventory;

public interface InventoryService {
    
    public List<Inventory> getAllItems();

    public Inventory getItemById(Long id);

    public void updateStock(Collection<Inventory> items);
}
