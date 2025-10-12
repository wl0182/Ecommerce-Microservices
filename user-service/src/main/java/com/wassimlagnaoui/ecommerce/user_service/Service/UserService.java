package com.wassimlagnaoui.ecommerce.user_service.Service;


import com.wassimlagnaoui.ecommerce.user_service.DTO.*;
import com.wassimlagnaoui.ecommerce.user_service.Exceptions.UserNotFoundException;
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
    // Leaving Login and Authentication to Auth Service for Later

    // update user
    public UserDetails updateUser(Long id, UpdateUserDTO userDTO){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        if(userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if(userDTO.getName() != null) user.setName(userDTO.getName());
        if(userDTO.getPhoneNumber() != null) user.setPhoneNumber(userDTO.getPhoneNumber());
        User updatedUser = userRepository.save(user);


        return UserDetails.builder().email(updatedUser.getEmail())
                .id(updatedUser.getId()).name(updatedUser.getName()).phoneNumber(updatedUser.getPhoneNumber()).build();
    }


    // get User Addresses by user id
    @Transactional(readOnly = true)
    public List<AddressDTO> getUserAddresses(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return user.getAddresses().stream()
                .map(address -> new AddressDTO(address.getId(), address.getStreet(), address.getCity(), address.getZip(), address.getCountry(), address.isDefault()))
                .toList();

    }


    public AddressDTO addAddressToUser(Long userId, AddAddressDTO addAddressDTO){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Address address = new Address();

        address.setStreet(addAddressDTO.getStreet());
        address.setCity(addAddressDTO.getCity());
        address.setZip(addAddressDTO.getZip());
        address.setCountry(addAddressDTO.getCountry());

        // remove default from other addresses if this one is default
        if(addAddressDTO.isDefault()){
            user.getAddresses().forEach(addr -> addr.setDefault(false));
            address.setDefault(addAddressDTO.isDefault());
        }

        address.setUser(user);

        user.getAddresses().add(address);
        User updatedUser = userRepository.save(user);

        Address savedAddress = updatedUser.getAddresses().get(updatedUser.getAddresses().size() - 1); // get the last added address

        return new AddressDTO(savedAddress.getId(), savedAddress.getStreet(), savedAddress.getCity(), savedAddress.getZip(), savedAddress.getCountry(), savedAddress.isDefault());

    }



}
