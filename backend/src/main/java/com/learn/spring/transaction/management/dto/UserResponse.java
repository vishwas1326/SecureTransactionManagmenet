package com.learn.spring.transaction.management.dto;

import com.learn.spring.transaction.management.entity.Role;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role)
{}

