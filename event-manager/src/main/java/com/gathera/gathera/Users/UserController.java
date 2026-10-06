package com.gathera.gathera.Users;

import com.gathera.gathera.security.jwt.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UsersDtoConverter usersDtoConverter;

    public UserController(
            AuthenticationService authenticationService,
            UserService userService, UsersDtoConverter usersDtoConverter) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.usersDtoConverter = usersDtoConverter;
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticationUser(
            @RequestBody @Valid SignInRequest signInRequest
    ){
        log.info("Get post request for auth user with signIn request: login={}", signInRequest.login());

        var token = authenticationService.authenticateUser(signInRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new JwtTokenResponse(token));
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ){
        log.info("Get post request for created user with signUp request: login={}", signUpRequest.login());

        var user = userService.registerUser(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usersDtoConverter.toDto(user));
    }

    @GetMapping("/{usersId}")
    public UserDto findUsersById(
            @PathVariable("usersId") Long id
    ){
        log.info("Get request for finding user by id: id={}", id);
        var foundUser = userService.findUserById(id);
        return usersDtoConverter.toDto(foundUser);
    }

}
