package com.wassimlagnaoui.ecommerce.user_service.Controller;


import com.wassimlagnaoui.ecommerce.user_service.DTO.*;
import com.wassimlagnaoui.ecommerce.user_service.Model.Address;
import com.wassimlagnaoui.ecommerce.user_service.Service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
       return ResponseEntity.ok(userDetails);
    }
    // get all users
    @GetMapping("/all")
    public ResponseEntity<List<UserDetails>> getAllUsers() {
        List<UserDetails> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Create user
    @PostMapping("/deprecated-create")
    public ResponseEntity<UserDetails> createUser(@Valid @RequestBody CreateUserDTO userDetails) {
        UserDetails createdUser = userService.createUser(userDetails);
        return ResponseEntity.ok(createdUser);
    }


    // Register user
    @PostMapping("/register")
    public ResponseEntity<UserDetails> registerUser(@Valid @RequestBody RegisterUserDTO registerUserDTO) {
        UserDetails registeredUser = userService.registerUser(registerUserDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.loginUser(loginRequest);
        if (loginResponse != null) {
            return ResponseEntity.ok(loginResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Unauthorized
        }
    }

    //update user
    @PutMapping("/{id}")
    public ResponseEntity<UserDetails> updateUser(@PathVariable("id") Long id,@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserDetails updatedUser = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(updatedUser);

    }

    // get Addresses by user id
    @GetMapping("/{id}/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(@PathVariable Long id) {
        List<AddressDTO> addresses = userService.getUserAddresses(id);
        return ResponseEntity.ok(addresses);
    }



    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressDTO> addAddressToUser(@PathVariable Long id, @Valid @RequestBody AddAddressDTO addAddressDTO) {
        AddressDTO addressDTO = userService.addAddressToUser(id, addAddressDTO);
        return ResponseEntity.ok(addressDTO);
    }

    // validate address curl command localhost 7001 : GET http://localhost:7001/api/users/1/addresses/1/validate
    @GetMapping("/{userId}/addresses/{addressId}/validate")
    public ResponseEntity<Boolean> validateAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        Boolean isValid = userService.validateAddress(userId, addressId);
        return ResponseEntity.ok(isValid);
    }

    // delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }













}
