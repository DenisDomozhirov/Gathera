package com.gathera.gathera.security.jwt;

import com.gathera.gathera.Users.SignInRequest;
import com.gathera.gathera.Users.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final JwtTokenManager jwtTokenManager;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(JwtTokenManager jwtTokenManager, AuthenticationManager authenticationManager) {
        this.jwtTokenManager = jwtTokenManager;
        this.authenticationManager = authenticationManager;
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

        return jwtTokenManager.generateJwtToken(signInRequest.login());
    }

    public User getCurrentAuthenticatedUserOrThrow(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new IllegalStateException("Authentication not present");
        }

        return (User) authentication.getPrincipal();
    }
}
