package com.example.jwtauth.repository;

import com.example.jwtauth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User,Long> {


    User findByEmail(String email);

    User findByUserNameOrEmail(String username, String email);



    User findByUserName(String username);

    Boolean existsByUserName(String username);

    Boolean existsByEmail(String email);


    User findByUserNameAndPassword(String login,String pwd);

    User findByEmailAndPassword(String email,String pwd);

}
