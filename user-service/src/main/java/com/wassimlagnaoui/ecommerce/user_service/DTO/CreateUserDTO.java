package com.wassimlagnaoui.ecommerce.user_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    @NonNull
    private String email;
    @NonNull
    private String name;

    @NonNull
    private String password;


    private String phoneNumber;

    private String addressStreet;
    private String addressCity;
    private String addressZip;
    private String addressCountry;

}
