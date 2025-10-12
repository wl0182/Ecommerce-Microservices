package com.wassimlagnaoui.ecommerce.user_service.Controller;


import com.wassimlagnaoui.ecommerce.user_service.DTO.*;
import com.wassimlagnaoui.ecommerce.user_service.Model.Address;
import com.wassimlagnaoui.ecommerce.user_service.Service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // Inject UserService
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Long id) {
        UserDetails userDetails = userService.getUserById(id);
        if (userDetails != null) {
            return ResponseEntity.ok(userDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create user
    @PostMapping("/deprecated-create")
    public ResponseEntity<UserDetails> createUser(@RequestBody CreateUserDTO userDetails) {
        UserDetails createdUser = userService.createUser(userDetails);
        return ResponseEntity.ok(createdUser);
    }


    // Register user
    @PostMapping("/register")
    public ResponseEntity<UserDetails> registerUser(@RequestBody RegisterUserDTO registerUserDTO) {
        UserDetails registeredUser = userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(registeredUser);
    }

    //update user
    @PutMapping("/{id}")
    public ResponseEntity<UserDetails> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        UserDetails updatedUser = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(updatedUser);

    }

    // get Addresses by user id
    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@PathVariable Long id) {
        List<AddressDTO> addresses = userService.getUserAddresses(id);
        return ResponseEntity.ok(addresses);
    }


    // add address to user
    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressDTO> addAddressToUser(@PathVariable Long id, @RequestBody AddAddressDTO addAddressDTO) {
        AddressDTO addressDTO = userService.addAddressToUser(id, addAddressDTO);
        return ResponseEntity.ok(addressDTO);
    }













}
