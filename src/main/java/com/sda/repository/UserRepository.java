package com.sda.repository;

import com.sda.entity.Role;
import com.sda.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email =:email")
    User findUsersByEmail(String email);

    @Query("SELECT u FROM User u " +
            "join u.SignUpEvents s " +
            "WHERE s.id =:id")
    List<User> findBySignUpEventsContains(int id);

    List<User> findAll();

    Page<User> findAllByRoles(Pageable pageable, Role rol);

    List<User> findAllByRoles(Role role);


}
