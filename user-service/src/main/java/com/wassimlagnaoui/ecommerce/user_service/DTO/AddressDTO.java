package com.wassimlagnaoui.ecommerce.user_service.DTO;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressDTO {
    private Long id;
    private String street;
    private String city;
    private String zip;
    private String country;
    private boolean isDefault;
}
