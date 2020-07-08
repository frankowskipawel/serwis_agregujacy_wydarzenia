package com.sda.repository;

import com.sda.entity.Event;
import com.sda.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.email =:email")
    User findUsersByEmail(String email);

//    select r from Recipe r
//    join r.ingredientList i
//    where i.name = :ingredient
    @Query("SELECT u FROM User u " +
            "join u.SignUpEvents s " +
            "WHERE s.id =:id")
    List<User> findBySignUpEventsContains(int id);


}
