package com.example.warehouse.backend.csv;

import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryImportTest {

    private InventoryRepository inventoryRepository;
    private ResourceLoader resourceLoader;
    private ObjectMapper objectMapper;
    private InventoryImport inventoryImport;

    @BeforeEach
    void setUp() {
        inventoryRepository = mock(InventoryRepository.class);
        resourceLoader = mock(ResourceLoader.class);
        objectMapper = new ObjectMapper();
        inventoryImport = new InventoryImport(inventoryRepository, resourceLoader, objectMapper);
    }

    @Test
    void loadInventoryFromJson_savesNewInventoryItems() throws Exception {
        // prepare
        String json = "{ \"inventory\": [ { \"art_id\": \"1\", \"name\": \"leg\", \"stock\": \"12\" } ] }";

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());
        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource(any())).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);

        when(inventoryRepository.existsById(1L)).thenReturn(false);

        // execute
        inventoryImport.run(new String[]{});

        // assert
        verify(inventoryRepository, times(1)).saveAll(argThat(InventoryImportTest::containsExpectedInventory));
    }

    private static boolean containsExpectedInventory(List<Inventory> list) {
        if (list == null || list.size() != 1) return false;
        Inventory inv = list.get(0);
        return inv.getId() == 1L // confirm correct
            && "leg".equals(inv.getName()) // confirm correct name
            && inv.getStock() == 12; // confirm correct stock
    }
}