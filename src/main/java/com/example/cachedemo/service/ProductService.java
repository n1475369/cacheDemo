package com.example.cachedemo.service;

import com.example.cachedemo.jpa.Product;
import com.example.cachedemo.jpa.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

//    @Transactional
    public void testCache() {
        // 保存一條數據
        Product product = save("JCache Product",99.99);
        Long id = product.getId();

        // 第一次查詢：應從數據庫讀取並加入cache
        Product fetched1 = getProductById(id);

        // 第二次查詢：應直接從cache讀取
        Product fetched2 = getProductById(id);
    }

    public void testCacheOOM() {
        for (int i = 0; i < 10000; i++) {
            Product product = save(UUID.randomUUID().toString(), i * 10.0);

            // 不斷查詢，確保每個數據都進入cache
            Product fetched = getProductById(product.getId());
        }
    }

    public void testSaveAllPerformance() {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Product product = new Product();
            product.setName(UUID.randomUUID().toString());
            product.setPrice(i * 10.0);
            products.add(product);
        }

        long start = System.currentTimeMillis();
        productRepository.saveAll(products);
        long end = System.currentTimeMillis();

        System.out.println("時間: " + (end - start) + "ms");
    }


    public Product save(String name, double price) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return productRepository.save(product);
    }

    public Product getProductById(final Long id) {
        return productRepository.findById(id).orElseThrow();
    }
}
