package com.example.gem_springboot.modules.users;

import java.util.List;
import com.example.gem_springboot.modules.users.UserResponse;


public record UsersList(List<UserResponse> users, long total) {}
