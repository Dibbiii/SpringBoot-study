package com.example.gem_springboot.controller;

import com.example.gem_springboot.dto.UserResponse;
import com.example.gem_springboot.models.UserModel;
import com.example.gem_springboot.services.UserService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @GetMapping
    public UserResponse findAllPaginated(
        @RequestParam(defaultValue = "0") int skip,
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<UserModel> paginatedUsers = userService.findPaginated(skip, limit);
        long totalRecords = userService.count();

        return new UserResponse(paginatedUsers, totalRecords);
    }

    @GetMapping("/{id}")
    public UserModel findById(@PathVariable Long id) {
        return userService.findById(id).orElse(null);
    }

    @PostMapping
    public UserModel createUser(@RequestBody UserModel user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public UserModel updateUser(
        @RequestBody UserModel user,
        @PathVariable Long id
    ) {
        return userService.updateUser(user, id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? "User deleted" : "User not found";
    }
}
