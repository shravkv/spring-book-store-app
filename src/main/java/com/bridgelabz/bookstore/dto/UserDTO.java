package com.bridgelabz.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private String firstName;

    private String lastName;
    @Email

    private String email;

    private String address;

    private String password;

    private boolean isAdmin;
}
