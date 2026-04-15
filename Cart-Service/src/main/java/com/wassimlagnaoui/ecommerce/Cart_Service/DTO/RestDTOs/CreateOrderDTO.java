package com.wassimlagnaoui.ecommerce.Cart_Service.DTO.RestDTOs;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {


    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;

    // adding Validation for paymentMethod and addressId can be done here
    private String paymentMethod;
    @NotNull(message = "Address ID cannot be null")
    private Long addressId;





}
