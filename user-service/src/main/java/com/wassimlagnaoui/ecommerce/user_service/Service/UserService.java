package com.wassimlagnaoui.ecommerce.user_service.Service;


import com.wassimlagnaoui.ecommerce.user_service.DTO.CreateUserDTO;
import com.wassimlagnaoui.ecommerce.user_service.DTO.RegisterUserDTO;
import com.wassimlagnaoui.ecommerce.user_service.DTO.UserDetails;
import com.wassimlagnaoui.ecommerce.user_service.Model.Address;
import com.wassimlagnaoui.ecommerce.user_service.Model.User;
import com.wassimlagnaoui.ecommerce.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    // inject PasswordEncoder
    @Autowired
    @Qualifier("PasswordEncoder")
    private BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // get user by id
    @Transactional(readOnly = true)
    public UserDetails getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserDetails(user.getId(), user.getEmail(), user.getName(), user.getPhoneNumber()))
                .orElse(null);
    }

    // create user
    @Transactional
    public UserDetails createUser(CreateUserDTO userDTO){
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());

        user.setPhoneNumber(userDTO.getPhoneNumber());
        User savedUser = userRepository.save(user);
        return new UserDetails(savedUser.getId(), savedUser.getEmail(), savedUser.getName(), savedUser.getPhoneNumber());
    }

    // register user with email and password
    @Transactional
    public UserDetails registerUser(RegisterUserDTO registerUserDTO){
        User user = new User();
        Address address = new Address();

        user.setEmail(registerUserDTO.getEmail());
        user.setName(registerUserDTO.getName());
        user.setPhoneNumber(registerUserDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));

        address.setStreet(registerUserDTO.getAddressStreet());
        address.setCity(registerUserDTO.getAddressCity());
        address.setZip(registerUserDTO.getAddressZip());
        address.setCountry(registerUserDTO.getAddressCountry());
        address.setDefault(true);
        address.setUser(user);

        user.setAddresses(List.of(address));

        User savedUser = userRepository.save(user);

        return new UserDetails(savedUser.getId(), savedUser.getEmail(), savedUser.getName(), savedUser.getPhoneNumber());

    }



}
