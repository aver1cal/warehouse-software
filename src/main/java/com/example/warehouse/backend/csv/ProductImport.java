package com.example.warehouse.backend.csv;

import com.example.warehouse.backend.model.Product;
import com.example.warehouse.backend.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import com.example.warehouse.backend.model.ContainedArticle;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductImport implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductImport.class);

    private final ProductRepository productRepository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Value("${app.resource.product}")
    private String jsonPath;

    public ProductImport(ProductRepository productRepository, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.productRepository = productRepository;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        loadProductsFromJson(jsonPath);
    }

    private void loadProductsFromJson(String jsonPath) {
        try {
            logger.info("Loading products...");
            Resource resource = resourceLoader.getResource(jsonPath);

            try (InputStream inputStream = resource.getInputStream()) {
                JsonNode root = objectMapper.readTree(inputStream);
                List<Product> productsFromJson = objectMapper.readValue(
                    root.get("products").traverse(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class)
                );

                if (ObjectUtils.isEmpty(productsFromJson)) {
                    logger.info("No products json found.");
                    return;
                }
                List<Product> productsToSave = getProductsToSave(productsFromJson);

                productRepository.saveAll(productsToSave);
                logger.info("Successfully synchronized products.");
            }
        } catch (IOException e) {
            logger.error("Error reading JSON file: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while loading products: {}", e.getMessage(), e);
        }
    }

    private List<Product> getProductsToSave(List<Product> productsFromJson) {
        List<Product> productsToSave = new ArrayList<>();
        for (Product product : productsFromJson) {
            if (product.getId() != null && productRepository.existsById(product.getId())) {
                productRepository.findById(product.getId()).ifPresent(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setStock(product.getStock());
                    existingProduct.setArticles(product.getArticles());
                    productsToSave.add(existingProduct);
                });
            } else {
                // also check if a product with the same name exists
                boolean alreadyExists = productRepository.existsByName(product.getName());
                if (!alreadyExists) {
                    productsToSave.add(product);
                }
            }
        }
        return productsToSave;
    }
}