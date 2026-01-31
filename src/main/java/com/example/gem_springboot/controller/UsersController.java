package com.example.gem_springboot.controller;

import com.example.gem_springboot.models.UserModel;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

    List<UserModel> UsersDatabase = List.of(
        new UserModel("1", "John Doe", "john@example.com"),
        new UserModel("2", "Jane Tin", "jane@example.com")
    );

    @GetMapping
    public List<UserModel> getAllUsers() {
        return UsersDatabase;
    }
    
    @GetMapping("/{id}")
    public UserModel getUser(@PathVariable String id){
        return UsersDatabase.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    @PostMapping
    public UserModel createUser(@RequestBody UserModel user) {
        UsersDatabase.add(user);
        return user;
    }

    @PutMapping("/{id}")
    public UserModel updateUser(
        @RequestBody UserModel user,
        @PathVariable String id
    ) {
        return UsersDatabase.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .map(u -> {
                u.setUsername(user.getUsername());
                u.setEmail(user.getEmail());
                return u;
            })
            .orElse(null);
    }

    @DeleteMapping("/{id}")
    public UserModel deleteUser(@PathVariable String id) {
        return UsersDatabase.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .map(u -> {
                UsersDatabase.remove(u);
                return u;
            })
            .orElse(null);
    }
}
