package com.wassimlagnaoui.ecommerce.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    // { name, email, password }
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}
