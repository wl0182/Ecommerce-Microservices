package com.wassimlagnaoui.ecommerce.product_service.Repository;

import com.wassimlagnaoui.ecommerce.product_service.Domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
