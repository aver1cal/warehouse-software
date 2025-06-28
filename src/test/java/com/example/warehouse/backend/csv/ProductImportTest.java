package com.example.warehouse.backend.csv;

import com.example.warehouse.backend.model.Product;
import com.example.warehouse.backend.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductImportTest {

    private ProductRepository productRepository;
    private ResourceLoader resourceLoader;
    private ObjectMapper objectMapper;
    private ProductImport productImport;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        resourceLoader = mock(ResourceLoader.class);
        objectMapper = new ObjectMapper();
        productImport = new ProductImport(productRepository, resourceLoader, objectMapper);
    }

    @Test
    void loadProductsFromJson_savesNewProductItems() throws Exception {
        // prepare
        String json = "{ \"products\": [ { \"name\": \"Dining Chair\", \"contain_articles\": [ { \"art_id\": \"1\", \"amount_of\": \"4\" } ], \"price\": 20 } ] }";

        InputStream inputStream = new ByteArrayInputStream(json.getBytes());
        Resource resource = mock(Resource.class);
        when(resourceLoader.getResource(any())).thenReturn(resource);
        when(resource.getInputStream()).thenReturn(inputStream);

        when(productRepository.existsById(any())).thenReturn(false);
        when(productRepository.existsByName("Dining Chair")).thenReturn(false);

        // execute
        productImport.run(new String[]{});

        // assert
        verify(productRepository, times(1)).saveAll(argThat(ProductImportTest::containsExpectedProduct));
    }

    private static boolean containsExpectedProduct(List<Product> list) {
        if (list == null || list.size() != 1) return false;
        Product product = list.get(0);
        return "Dining Chair".equals(product.getName()) // confirm correct name
            // confirm articles matching
            && product.getArticles() != null
            && product.getArticles().size() == 1
            && product.getArticles().get(0).getInventoryId() == 1L
            && product.getArticles().get(0).getQuantity() == 4
            && "20".equals(product.getPrice()); // confirm correct price
    }
}