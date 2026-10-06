package com.gathera.gathera.Users;

public record UserDto(
        Long id,
        String login,
        Integer age,
        UserRole role
) {
}
