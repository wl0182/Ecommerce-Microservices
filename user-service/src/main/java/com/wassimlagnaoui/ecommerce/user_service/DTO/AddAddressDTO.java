package com.wassimlagnaoui.ecommerce.user_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAddressDTO {
    private String street;
    private String city;
    private String zip;
    private String country;
    private boolean isDefault;
}
