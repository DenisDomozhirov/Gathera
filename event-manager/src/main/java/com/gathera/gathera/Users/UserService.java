package com.gathera.gathera.Users;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityUserConverter entityUserConverter;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EntityUserConverter entityUserConverter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityUserConverter = entityUserConverter;
    }

    public User registerUser (SignUpRequest signUpRequest) {
        if(userRepository.existsByLogin(signUpRequest.login())){
            throw new IllegalArgumentException("This username already taken");
        }

        var hashedPass = passwordEncoder.encode(signUpRequest.password());

        var userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                hashedPass,
                signUpRequest.age(),
                UserRole.USER.name()
        );

        var saved = userRepository.save(userToSave);

        return entityUserConverter.toDomain(saved);
    }

    public User findUserByLogin(String loginFromToken) {
        var findUser = userRepository.findByLogin(loginFromToken)
                .orElseThrow(() -> new EntityNotFoundException("Not found user with this login"));

        return entityUserConverter.toDomain(findUser);
    }

    public User findUserById(Long id) {
        var foundUserId = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with this id = %s".formatted(id)));
        return entityUserConverter.toDomain(foundUserId);
    }
}
