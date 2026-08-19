package com.gathera.gathera.Users;

import org.springframework.stereotype.Component;

@Component
public class UsersDtoConverter {

    public UserDto toDto(User user){
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }

}
