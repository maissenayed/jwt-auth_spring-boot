package com.example.jwtauth.repository;



import com.example.jwtauth.model.Role;
import com.example.jwtauth.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepo extends JpaRepository<Role,Long> {

    @Query (value = "SELECT * FROM Roles where name IN (:roles)", nativeQuery = true)
    Set<Role> find(@Param ("roles") List<String> roles);

    Optional<Role> findByName(RoleType roleName);

}
