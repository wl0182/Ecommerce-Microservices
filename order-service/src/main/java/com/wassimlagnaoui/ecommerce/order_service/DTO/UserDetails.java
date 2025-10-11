package com.wassimlagnaoui.ecommerce.order_service.DTO;

import lombok.*;
import lombok.Data;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    private long id;
    private String email;
    private String name;
}
