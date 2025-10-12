package com.wassimlagnaoui.ecommerce.user_service.Controller;


import com.wassimlagnaoui.ecommerce.user_service.DTO.CreateUserDTO;
import com.wassimlagnaoui.ecommerce.user_service.DTO.RegisterUserDTO;
import com.wassimlagnaoui.ecommerce.user_service.DTO.UserDetails;
import com.wassimlagnaoui.ecommerce.user_service.Service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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






}
