package com.example.warehouse.backend.service.product;

import com.example.warehouse.backend.model.ContainedArticle;
import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.model.Product;
import com.example.warehouse.backend.repository.InventoryRepository;
import com.example.warehouse.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private InventoryRepository inventoryRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        inventoryRepository = mock(InventoryRepository.class);
        productService = new ProductServiceImpl(productRepository, inventoryRepository);
    }

    @Test
    void getAllProducts_returnsProductsWithCorrectStock() {
        // prepare
        Inventory leg = new Inventory();
        leg.setId(1L);
        leg.setName("leg");
        leg.setStock(10);

        ContainedArticle article = new ContainedArticle();
        article.setInventoryId(1L);
        article.setQuantity(2);

        Product product = new Product();
        product.setId(1L);
        product.setName("Chair");
        product.setArticles(List.of(article));

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(leg));

        // execute
        List<Product> products = productService.getAllProducts();

        // assert
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getStock()).isEqualTo(5);
    }

    @Test
    void getProductById_returnsProductWithCorrectStock() {
        // prepare
        Inventory screw = new Inventory();
        screw.setId(2L);
        screw.setName("screw");
        screw.setStock(8);

        ContainedArticle article = new ContainedArticle();
        article.setInventoryId(2L);
        article.setQuantity(4);

        Product product = new Product();
        product.setId(2L);
        product.setName("Table");
        product.setArticles(List.of(article));

        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findById(2L)).thenReturn(Optional.of(screw));

        // execute
        Product result = productService.getProductById(2L);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getStock()).isEqualTo(2);
    }

    @Test
    void getProductById_returnsZeroStockIfComponentMissing() {
        // prepare
        ContainedArticle article = new ContainedArticle();
        article.setInventoryId(99L);
        article.setQuantity(1);

        Product product = new Product();
        product.setId(3L);
        product.setName("MissingPart");
        product.setArticles(List.of(article));

        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());

        // execute
        Product result = productService.getProductById(3L);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getStock()).isEqualTo(0);
    }
}