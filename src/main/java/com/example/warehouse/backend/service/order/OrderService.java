package com.example.warehouse.backend.service.order;

import com.example.warehouse.backend.api.v1.OrderRequest;
import com.example.warehouse.backend.model.Order;

public interface OrderService {
    
    public Order processNewOrder(OrderRequest request);

    public Order getOrderById(Long orderId);
}
