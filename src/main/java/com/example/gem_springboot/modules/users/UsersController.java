package com.example.gem_springboot.modules.users;

import com.example.gem_springboot.modules.users.internal.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService; // inietto UserService

    @GetMapping
    public UsersList findAllPaginated(
        @RequestParam(defaultValue = "0") int skip,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String order,
        @RequestParam(required = false) String q
    ) {
        return userService.findAllPaginated(q, sortBy, order, skip, limit);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id).orElse(null);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
        @RequestBody UserRequest request,
        @PathVariable Long id
    ) {
        return userService.updateUser(request, id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? "User deleted" : "User not found";
    }
}
