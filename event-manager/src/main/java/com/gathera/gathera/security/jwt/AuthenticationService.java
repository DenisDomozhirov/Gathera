package com.gathera.gathera.security.jwt;

import com.gathera.gathera.Users.SignInRequest;
import com.gathera.gathera.Users.User;
import com.gathera.gathera.Users.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final JwtTokenManager jwtTokenManager;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthenticationService(
            JwtTokenManager jwtTokenManager,
            AuthenticationManager authenticationManager,
            UserService userService) {
        this.jwtTokenManager = jwtTokenManager;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public String authenticateUser(
            SignInRequest signInRequest
    ){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );

        User user = userService.findUserByLogin(signInRequest.login());
        return jwtTokenManager.generateJwtToken(user.login(), user.id());
    }

    public User getCurrentAuthenticatedUserOrThrow(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new IllegalStateException("Authentication not present");
        }

        return (User) authentication.getPrincipal();
    }
}
