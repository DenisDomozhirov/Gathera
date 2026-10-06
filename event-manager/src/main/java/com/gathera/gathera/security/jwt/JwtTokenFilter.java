package com.gathera.gathera.security.jwt;

import com.gathera.gathera.Users.User;
import com.gathera.gathera.Users.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;

    public JwtTokenFilter(JwtTokenManager jwtTokenManager, UserService userService) {
        this.jwtTokenManager = jwtTokenManager;
        this.userService = userService;


    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authenticationHeader == null ||
                !authenticationHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        var jwtToken = authenticationHeader.substring(7);

        String loginFromToken;

        try{
            loginFromToken = jwtTokenManager.getLoginFromToken(jwtToken);
        } catch (JwtException | IllegalArgumentException e){
            log.error("Error while reading jwt: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        User userLogin = userService.findUserByLogin(loginFromToken);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userLogin,
                null,
                List.of(new SimpleGrantedAuthority(userLogin.role().toString()))
        );

        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);

    }
}
