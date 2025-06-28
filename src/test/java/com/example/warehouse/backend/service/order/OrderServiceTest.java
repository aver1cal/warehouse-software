package com.example.warehouse.backend.service.order;

import com.example.warehouse.backend.api.v1.OrderRequest;
import com.example.warehouse.backend.model.*;
import com.example.warehouse.backend.repository.OrderRepository;
import com.example.warehouse.backend.service.inventory.InventoryService;
import com.example.warehouse.backend.service.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private InventoryService inventoryService;
    private ProductService productService;
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        inventoryService = mock(InventoryService.class);
        productService = mock(ProductService.class);
        orderService = new OrderServiceImpl(orderRepository, inventoryService, productService);
    }

    @Test
    void processNewOrder_successfulOrder_updatesInventoryAndSavesOrder() {
        // prepare
        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(1L);
        orderedProduct.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setCustomerName("Alice");
        request.setOrderDate(LocalDate.now());
        request.setProducts(List.of(orderedProduct));

        ContainedArticle article = new ContainedArticle();
        article.setInventoryId(10L);
        article.setQuantity(3);

        Product product = new Product();
        product.setId(1L);
        product.setName("Table");
        product.setArticles(List.of(article));
        product.setStock(5);

        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setName("leg");
        inventory.setStock(10);

        when(productService.getProductById(1L)).thenReturn(product);
        when(inventoryService.getItemById(10L)).thenReturn(inventory);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // execute
        Order result = orderService.processNewOrder(request);

        // assert
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PROCESSED);
        verify(inventoryService).updateStock(anyCollection());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void processNewOrder_outOfStockOrder_savesOrderWithOutOfStockStatus() {
        // prepare
        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setProductId(1L);
        orderedProduct.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setCustomerName("Bob");
        request.setOrderDate(LocalDate.now());
        request.setProducts(List.of(orderedProduct));

        Product product = new Product();
        product.setId(1L);
        product.setName("Table");
        product.setArticles(List.of());
        product.setStock(0); // Out of stock

        when(productService.getProductById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // execute
        Order result = orderService.processNewOrder(request);

        // assert
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.OUT_OF_STOCK);
        verify(inventoryService, never()).updateStock(anyCollection());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrderById_returnsOrder() {
        // prepare
        Order order = new Order("Alice", LocalDate.now(), Order.OrderStatus.PROCESSED);
        order.setId(123L);
        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        // execute
        Order result = orderService.getOrderById(123L);

        // assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(123L);
    }

    @Test
    void getOrderById_returnsNullIfNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Order result = orderService.getOrderById(999L);

        assertThat(result).isNull();
    }
}