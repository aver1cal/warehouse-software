package com.example.warehouse.backend.csv;

import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.repository.InventoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class InventoryImport implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryImport.class);

    private final InventoryRepository inventoryRepository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Value("${app.resource.inventory}")
    private String jsonPath;

    public InventoryImport(InventoryRepository inventoryRepository, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.inventoryRepository = inventoryRepository;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadInventoryFromJson(jsonPath);
    }

    private void loadInventoryFromJson(String jsonPath) {
        try {
            logger.info("Loading inventory...");
            Resource resource = resourceLoader.getResource(jsonPath);

            try (InputStream inputStream = resource.getInputStream()) {
                JsonNode root = objectMapper.readTree(inputStream);
                List<Inventory> itemsFromJson = objectMapper.readValue(
                    root.get("inventory").traverse(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Inventory.class)
                );

                if (ObjectUtils.isEmpty(itemsFromJson)) {
                    logger.info("No inventory json found.");
                    return;
                }
                List<Inventory> itemsToSave = getItemsToSave(itemsFromJson);

                inventoryRepository.saveAll(itemsToSave);
                logger.info("Successfully synchronized inventory.");
            }
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while loading inventory: {}", e.getMessage(), e);
        }
    }

    private List<Inventory> getItemsToSave(List<Inventory> itemsFromJson) {
        List<Inventory> itemsToSave = new ArrayList<>();
        for (Inventory item : itemsFromJson) {
            if (item.getId() != null && inventoryRepository.existsById(item.getId())) {
                Inventory existingItem = inventoryRepository.findById(item.getId()).orElse(null);
                if (existingItem != null) {
                    existingItem.setName(item.getName());
                    existingItem.setStock(item.getStock());
                }
            } else {
                itemsToSave.add(item);
            }
        }
        return itemsToSave;
    }
}