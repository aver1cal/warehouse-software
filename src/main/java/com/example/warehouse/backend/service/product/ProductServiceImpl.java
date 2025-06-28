package com.example.warehouse.backend.service.product;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.warehouse.backend.model.Inventory;
import com.example.warehouse.backend.model.ContainedArticle;
import com.example.warehouse.backend.model.Product;
import com.example.warehouse.backend.repository.InventoryRepository;
import com.example.warehouse.backend.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            int stock = checkProductStock(product.getArticles());
            product.setStock(stock);
        }
        return products;
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            int stock = checkProductStock(product.getArticles());
            product.setStock(stock);
        }
        return product;
    }

    private int checkProductStock(List<ContainedArticle> components) {
        int maxProductStock = Integer.MAX_VALUE;

        if (components == null || components.isEmpty()) {
            return 0;
        }

        for (ContainedArticle component : components) {
            Long itemId = component.getInventoryId();
            int quantityRequired = component.getQuantity();
            Inventory item = inventoryRepository.findById(itemId).orElse(null);

            // nonexistent article
            if (item == null) {
                logger.error("Component ID {} does not exist", itemId);
                return 0;
            }

            int partStock = item.getStock();
            int productStock = partStock / quantityRequired;
            maxProductStock = Math.min(maxProductStock, productStock);

            // missing stock
            if (maxProductStock == 0) {
                logger.debug("Missing stock for component ID {}", itemId);
                return 0;
            }
        }

        // resulting stock unchanged, returning 0
        if (maxProductStock == Integer.MAX_VALUE) {
            return 0;
        }

        return maxProductStock;
    }
}
