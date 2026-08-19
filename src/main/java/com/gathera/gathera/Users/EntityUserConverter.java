package com.gathera.gathera.Users;

import org.springframework.stereotype.Component;

@Component
public class EntityUserConverter {

    public User toDomain(UserEntity entity){
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getUserAge(),
                UserRole.valueOf(entity.getRole())
        );
    }
}
