package com.wassimlagnaoui.ecommerce.user_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NonNull
    private String email;

    @NonNull
    private String password;

}
