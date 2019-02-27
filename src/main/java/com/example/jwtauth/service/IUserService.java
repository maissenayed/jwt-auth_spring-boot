package com.example.jwtauth.service;


import com.example.jwtauth.model.User;

import java.util.List;

public interface IUserService {


    Iterable<User> findAll();

    User save(User u) ;

    User findOne(Long id);

    void delete(User u);

    User findByUserName(String login);

    User findByUserNameAndPassword(String login,String pwd);

    User findByEmailAndPassword(String login,String pwd);

    User getUserParEmail(String email);

    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);


}
