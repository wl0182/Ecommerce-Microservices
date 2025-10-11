package com.wassimlagnaoui.ecommerce.order_service.Repository;

import com.wassimlagnaoui.ecommerce.order_service.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {


    Optional<Product> findById(Long id);

    Optional<Product> findByName(String name);
}
