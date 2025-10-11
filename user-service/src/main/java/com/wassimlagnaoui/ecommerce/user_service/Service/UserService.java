package com.wassimlagnaoui.ecommerce.user_service.Service;


import com.wassimlagnaoui.ecommerce.user_service.DTO.CreateUserDTO;
import com.wassimlagnaoui.ecommerce.user_service.DTO.UserDetails;
import com.wassimlagnaoui.ecommerce.user_service.Model.User;
import com.wassimlagnaoui.ecommerce.user_service.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

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

}
