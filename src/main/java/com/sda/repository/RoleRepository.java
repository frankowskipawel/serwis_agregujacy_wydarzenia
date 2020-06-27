package com.sda.repository;

import com.sda.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RoleRepository extends JpaRepository<Role, Integer> {

//Role findByRole(Role role);



}
