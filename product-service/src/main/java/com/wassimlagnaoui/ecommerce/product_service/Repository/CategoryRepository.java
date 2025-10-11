package com.wassimlagnaoui.ecommerce.product_service.Repository;

import com.wassimlagnaoui.ecommerce.product_service.Domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
