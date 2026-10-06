package com.gathera.gathera.Users;

public record User(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
