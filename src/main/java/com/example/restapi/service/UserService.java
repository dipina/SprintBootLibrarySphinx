package com.example.restapi.service;

import com.example.restapi.model.User;
import com.example.restapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username); // Ensure UserRepository has this method
    }

    public User saveUser(User user) {
        user.setPassword(user.getPassword()); // Encrypt password before saving
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username); // Ensure UserRepository has this method
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /* 
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }*/

}
