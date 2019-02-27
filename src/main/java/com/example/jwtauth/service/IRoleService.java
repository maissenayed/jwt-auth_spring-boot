package com.example.jwtauth.service;

import com.example.jwtauth.model.Role;
import com.example.jwtauth.model.RoleType;

import java.util.Optional;

public interface IRoleService {
    Iterable<Role> findAll();

    Role save(Role u) ;

    Role findOne(Long id);

    void delete(Role u);

    Optional<Role> findByName(RoleType roleName);



}
