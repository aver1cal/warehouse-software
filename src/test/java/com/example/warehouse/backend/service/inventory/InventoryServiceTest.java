package com.example.warehouse.backend.service.inventory;

import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    private InventoryRepository inventoryRepository;
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        inventoryService = new InventoryServiceImpl(inventoryRepository);
    }

    @Test
    void getAllItems_returnsAllInventory() {
        // prepare
        Inventory item1 = new Inventory();
        item1.setId(1L);
        item1.setName("leg");
        item1.setStock(10);

        Inventory item2 = new Inventory();
        item2.setId(2L);
        item2.setName("screw");
        item2.setStock(20);

        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        // execute
        List<Inventory> result = inventoryService.getAllItems();

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("leg"); // check correct name
        assertThat(result.get(1).getStock()).isEqualTo(20); // check correct stock
    }

    @Test
    void getItemById_returnsInventoryById() {
        // prepare
        Inventory item = new Inventory();
        item.setId(1L);
        item.setName("leg");
        item.setStock(10);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(item));

        // execute
        Inventory result = inventoryService.getItemById(1L);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("leg");
    }

    @Test
    void getItemById_returnsNullIfNotFound() {
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

        Inventory result = inventoryService.getItemById(99L);

        assertThat(result).isNull();
    }

    @Test
    void updateStock_savesAllItems() {
        // prepare
        Inventory item1 = new Inventory();
        item1.setId(1L);
        item1.setName("leg");
        item1.setStock(5);

        Inventory item2 = new Inventory();
        item2.setId(2L);
        item2.setName("screw");
        item2.setStock(15);

        List<Inventory> items = Arrays.asList(item1, item2);

        // execute
        inventoryService.updateStock(items);

        // assert
        verify(inventoryRepository).saveAll(items);
    }
}