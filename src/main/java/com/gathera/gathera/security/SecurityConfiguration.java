package com.gathera.gathera.security;

import com.gathera.gathera.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final CustomUserDetailsService customUserDetailService;

    private final JwtTokenFilter jwtTokenFilter;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfiguration(CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                                 CustomUserDetailsService customUserDetailsService,
                                 JwtTokenFilter jwtTokenFilter,
                                 CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customUserDetailService = customUserDetailsService;
        this.jwtTokenFilter = jwtTokenFilter;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin(login -> login.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers(HttpMethod.DELETE, "/locations/{locationId}")
                                .hasAnyAuthority( "ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/locations/{locationId}")
                                .hasAnyAuthority( "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/locations")
                                .hasAnyAuthority( "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/locations")
                                .hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/users/{userId}")
                                .hasAnyAuthority("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/locations/{locationId}")
                                .hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.POST, "/users")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/users/auth")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/events")
                                .hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/events/{eventId}")
                                .hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.GET, "/events/my")
                                .hasAnyAuthority("USER")
                                .requestMatchers(HttpMethod.PUT, "/events/{eventId}")
                                .hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/events/{eventId}")
                                .hasAnyAuthority("USER", "ADMIN")
                                .requestMatchers("/events/registrations/**")
                                .hasAnyAuthority("USER")
                                .requestMatchers(HttpMethod.DELETE, "/events/search")
                                .hasAnyAuthority("USER", "ADMIN")
                                .anyRequest().authenticated())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        var authProvider = new DaoAuthenticationProvider(customUserDetailService);

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
