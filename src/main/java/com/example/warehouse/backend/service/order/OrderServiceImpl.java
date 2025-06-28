package com.example.warehouse.backend.service.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.warehouse.backend.api.v1.OrderRequest;
import com.example.warehouse.backend.model.ContainedArticle;
import com.example.warehouse.backend.model.Order;
import com.example.warehouse.backend.model.OrderedProduct;
import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.model.Product;
import com.example.warehouse.backend.model.Order.OrderStatus;
import com.example.warehouse.backend.repository.OrderRepository;
import com.example.warehouse.backend.service.inventory.InventoryService;
import com.example.warehouse.backend.service.product.ProductService;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final ProductService productService;

    public OrderServiceImpl(OrderRepository orderRepository, InventoryService inventoryService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @Transactional
    public Order processNewOrder(OrderRequest request) {
        logger.info("Received new order for customer: {}", request.getCustomerName());
        OrderStatus orderStatus = OrderStatus.PENDING;
        List<OrderedProduct> orderedProducts = request.getProducts();
        Map<Long, Inventory> orderedItems = new HashMap<>(); 
        for (OrderedProduct orderedProduct : orderedProducts) {
            Product product = productService.getProductById(orderedProduct.getProductId());
            int stock = product.getStock();
            if (stock < 1) {
                logger.warn("Order cancelled due to insufficient stock for product ID {}", 
                                product.getId());
                orderStatus = OrderStatus.OUT_OF_STOCK;
                break;
            }
            List<ContainedArticle> components = product.getArticles();
            for (ContainedArticle component : components) {
                int qty = component.getQuantity() * orderedProduct.getQuantity();
                Inventory item = orderedItems.get(component.getInventoryId());
                if (item == null) {
                    item = new Inventory(inventoryService.getItemById(component.getInventoryId()));
                }
                if (item.getStock() < qty) {
                    logger.warn("Order cancelled due to insufficient stock for component ID {}", 
                                item.getId());
                    orderStatus = OrderStatus.OUT_OF_STOCK;
                    break;
                }
                item.setStock(item.getStock() - qty);
                orderedItems.put(item.getId(), item);
            }
        }
        if (orderStatus == OrderStatus.PENDING) {
            inventoryService.updateStock(orderedItems.values());
            orderStatus = OrderStatus.PROCESSED;
            logger.info("Order for customer {} processed successfully", request.getCustomerName());
        }
        Order order = new Order(request.getCustomerName(), request.getOrderDate(), orderStatus);
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElse(null);
    }
}