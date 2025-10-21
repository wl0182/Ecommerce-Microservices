package com.wassimlagnaoui.ecommerce.Notification_Service.Repository;

import com.wassimlagnaoui.ecommerce.Notification_Service.Domain.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template,Long> {


    @Query("SELECT t FROM Template t WHERE t.type = ?1")
    Optional<Template> findByType(String type);


}
