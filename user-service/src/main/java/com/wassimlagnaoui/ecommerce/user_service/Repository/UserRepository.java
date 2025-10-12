package com.wassimlagnaoui.ecommerce.user_service.Repository;

import com.wassimlagnaoui.ecommerce.user_service.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT * FROM users u WHERE u.name ILIKE %?1%")
    List<User> findByNameContainingIgnoreCase(String name);



}
