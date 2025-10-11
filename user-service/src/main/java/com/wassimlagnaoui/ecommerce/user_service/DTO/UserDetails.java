package com.wassimlagnaoui.ecommerce.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.AccessType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetails {
    private long id;
    private String email;
    private String name;
    private String phoneNumber;

}
