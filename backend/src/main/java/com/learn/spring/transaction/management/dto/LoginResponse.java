package com.learn.spring.transaction.management.dto;

import com.learn.spring.transaction.management.entity.Role;

public record LoginResponse(
        String token,
        String tokenType,
        String username,
        Role role
) {
}
