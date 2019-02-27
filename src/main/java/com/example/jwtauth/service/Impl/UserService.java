package com.example.jwtauth.service.Impl;



import com.example.jwtauth.config.security.UserPrincipal;
import com.example.jwtauth.model.User;
import com.example.jwtauth.repository.UserRepo;
import com.example.jwtauth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Service(value = "UserService")
public class UserService implements IUserService, UserDetailsService {


    @Autowired
    private UserRepo userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;



    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User u)  {
        generateSecretToken(u);
        return userRepository.save(u);
    }
    private void generateSecretToken(User utilisateur) {
        utilisateur.setSecretToken(passwordEncoder.encode(
                utilisateur.getEmail() + utilisateur.getUserName() + utilisateur.getPassword() ));
    }

    @Override
    public User findOne(Long id) {
        return userRepository.findById(id).get();
    }

    @Override
    public void delete(User u) {
        userRepository.delete(u);
    }

    @Override
    public User findByUserName(String login) {
        System.out.println("on recherche par login:"+login);
        User u =  userRepository.findByUserName(login);
        System.out.println(u.getUserName());
        System.out.println(u.getPassword());
        return u;
    }

    @Override
    public User findByUserNameAndPassword(String login, String pwd) {
        return userRepository.findByUserNameAndPassword(login,pwd);
    }

    @Override
    public User findByEmailAndPassword(String login, String pwd) {
        return userRepository.findByEmailAndPassword(login,pwd);
    }

    @Override
    public User getUserParEmail(String email) {
        return userRepository.findByEmail(email);
    }



    @Override
    public Boolean existsByUserName(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return  userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUserNameOrEmail(s, s);
        return UserPrincipal.create(user);
    }


}
