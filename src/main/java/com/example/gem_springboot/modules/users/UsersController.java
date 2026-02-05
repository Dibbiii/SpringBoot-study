package com.example.gem_springboot.modules.users;

import com.example.gem_springboot.modules.posts.PostClient;
import com.example.gem_springboot.modules.posts.PostDTO;
import com.example.gem_springboot.modules.posts.PostService;
import com.example.gem_springboot.modules.users.UserFilter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService; // inietto UserService
    private final PostClient postClient;
    private final PostService postService;

    @GetMapping
    public UsersList findAllPaginated(
        @RequestParam(defaultValue = "0") int skip,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String order,
        UserFilter filter // Spring mappa automaticamente i valori di username e email dentro l'oggetto UserFilter
    ) {
        return userService.findAllPaginated(filter, sortBy, order, skip, limit);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id).orElse(null);
    }

    @PostMapping
    public UserResponse createUser(@RequestBody @Valid UserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(
        @RequestBody @Valid UserRequest request,
        @PathVariable Long id
    ) {
        return userService.updateUser(request, id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);
        return deleted ? "User deleted" : "User not found";
    }

    @GetMapping("/{id}/posts")
    public List<PostDTO> getUserPosts(@PathVariable Long id) {
        return postService.getPostsWithRetry(id);
    }
}
