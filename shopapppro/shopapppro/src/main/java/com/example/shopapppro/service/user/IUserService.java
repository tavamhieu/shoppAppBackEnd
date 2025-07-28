package com.example.shopapppro.service.user;

import com.example.shopapppro.dtos.UserDTO;
import com.example.shopapppro.exception.DataNotFoundException;
import com.example.shopapppro.models.User;

public interface IUserService  {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber ,String passwword) throws Exception;

}
