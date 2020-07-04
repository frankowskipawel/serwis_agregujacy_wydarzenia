package com.sda.entity;

import com.sda.controller.UniqueEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private int id;
    @NotEmpty
    @Size(max = 50, message = "Name can have up to 50 signs")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Name is empty, please enter valid name")
    private String name;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @Email
    @UniqueEmail(message = "account already exists!")
    private String email;
    @Size(min = 8)
    private String password;
    private boolean active;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

}
