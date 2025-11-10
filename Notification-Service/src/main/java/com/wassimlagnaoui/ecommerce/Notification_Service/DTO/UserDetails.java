package com.wassimlagnaoui.ecommerce.Notification_Service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
