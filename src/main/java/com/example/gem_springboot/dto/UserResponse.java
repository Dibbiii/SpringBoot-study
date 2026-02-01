package com.example.gem_springboot.dto;

import com.example.gem_springboot.models.UserModel;
import java.util.List;

public record UserResponse(List<UserModel> users, long total) {}
