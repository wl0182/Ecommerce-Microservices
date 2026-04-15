package com.wassimlagnaoui.ecommerce.user_service.Repository;


import com.wassimlagnaoui.ecommerce.user_service.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    // find all addresses by user id
    List<Address> findByUserId(Long userId);
    // find default address by user id
    @Query("SELECT a FROM Address a WHERE a.user.id = ?1 AND a.isDefault = true")
    Address findByUserIdAndIsDefaultTrue(Long userId);
}
