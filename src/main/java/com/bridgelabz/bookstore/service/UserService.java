package com.bridgelabz.bookstore.service;

import com.bridgelabz.bookstore.dto.UserDTO;
import com.bridgelabz.bookstore.exception.CustomException;
import com.bridgelabz.bookstore.model.UserData;
import com.bridgelabz.bookstore.repository.UserRepository;
import com.bridgelabz.bookstore.util.TokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenUtility tokenUtility;

    public UserData addUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()) == null) {
            UserData userData = new UserData(userDTO);
            userData = userRepository.save(userData);
            String token = tokenUtility.generateToken(userData.getUserID());
            userData.setToken(token);
            return userRepository.save(userData);
        } else
            throw new CustomException("User with email " + userDTO.getEmail() + " is already exists");
    }

    public List<UserData> getAllUsers() {
        if (!userRepository.findAll().isEmpty()) {
            return userRepository.findAll();
        } else
            throw new CustomException("Users Table is empty!");
    }

    public Optional<UserData> getUserById(Long id) {
        if (userRepository.existsById(id)) {
            return userRepository.findById(id);
        } else
            throw new CustomException("User id " + id + " Not found!");
    }

    public UserData getUserByEmail(String email) {
        if (userRepository.findByEmail(email) != null) {
            return userRepository.findByEmail(email);
        } else
            throw new CustomException("User with email " + email + " is Not Found");
    }

    public UserData updateUserByEmail(String email, UserDTO userDTO) {
        if (userRepository.findByEmail(email) != null) {
            long userID = userRepository.findByEmail(email).getUserID();
            UserData userData = new UserData(userDTO);
            userData.setUserID(userID);
            return userRepository.save(userData);
        } else
            throw new CustomException("User with email " + email + " is Not Found");
    }

    public String deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "User Deleted of id: " + id;
        } else
            throw new CustomException("User id " + id + " Not found!");
    }

    public String login(String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            UserData userData = userRepository.findByEmail(email);
            if (userData.getPassword().equals(password)) {
                userData.setLogin(true);
                userRepository.save(userData);
                return "Login SuccessFull";
            }else return "Incorrect password";
        } else
            throw new CustomException("User with email " + email + " is Not Found");
    }

    public String changePassword(String email, String token, String newPassword) {
        if (userRepository.findByEmail(email) != null) {
            UserData userData = userRepository.findByEmail(email);
            if (userData.getToken().equals(token)) {
                userData.setPassword(newPassword);
                userRepository.save(userData);
                return "Password Changed SuccessFull";
            }else return "Incorrect token";
        } else
            throw new CustomException("User with email " + email + " is Not Found");
    }
}
