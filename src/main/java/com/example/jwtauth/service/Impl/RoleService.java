package com.example.jwtauth.service.Impl;

import com.example.jwtauth.model.Role;
import com.example.jwtauth.model.RoleType;
import com.example.jwtauth.repository.RoleRepo;
import com.example.jwtauth.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service (value = " RoleService")
public class RoleService implements IRoleService {
    @Autowired
    RoleRepo roleRepository;
    @Override
    public Iterable<Role> findAll() {
        return roleRepository.findAll();}

    @Override
    public Role save(Role u) {
        return roleRepository.save(u);}


    @Override
    public Role findOne(Long id) {
        return roleRepository.findById(id).get();
    }

    @Override
    public void delete(Role u) {
        roleRepository.delete(u);

    }

    @Override
    public Optional<Role> findByName(RoleType roleName) {
        return roleRepository.findByName(roleName);
    }
}

