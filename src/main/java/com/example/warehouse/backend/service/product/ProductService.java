package com.example.warehouse.backend.service.product;

import java.util.List;

import com.example.warehouse.backend.model.Product;

public interface ProductService {
    
    public List<Product> getAllProducts();

    public Product getProductById(Long id);
}
