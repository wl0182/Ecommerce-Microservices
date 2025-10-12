package com.wassimlagnaoui.ecommerce.user_service.Repository;


import com.wassimlagnaoui.ecommerce.user_service.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
