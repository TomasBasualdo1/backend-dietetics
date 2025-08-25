package com.dietetic.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dietetic.backend.entity.Role;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // Rutas públicas
                        .requestMatchers(HttpMethod.GET,
                                "/categories/**",
                                "/products/**"
                        ).permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error/**").permitAll()
                        // Protegidas por User y Admin
                        .requestMatchers(HttpMethod.GET,
                                "/purchase-orders/user/{id}",
                                "/users/email/**",
                                "/users/{id}"
                        ).hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET,
                                "/purchase-orders/**"
                        ).hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST,
                                "/purchase-orders"
                        ).hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT,
                                "/users/{id}"
                        ).hasAnyAuthority(Role.USER.name(), Role.ADMIN.name())
                        // Protegidas por Admin
                        .requestMatchers(HttpMethod.POST,
                                "/categories",
                                "/products",
                                "/users"
                        ).hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.PUT,
                                "/categories/{id}",
                                "/products/{id}",
                                "/purchase-orders/{id}",
                                "/purchase-orders/{id}/confirm",
                                "/purchase-orders/{id}/cancel",
                                "/users/role/{id}"
                        ).hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE,
                                "/categories/{id}",
                                "/products/{id}",
                                "/purchase-orders/{id}",
                                "/users/{id}"
                        ).hasAuthority(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET,
                                "/users"
                        ).hasAuthority(Role.ADMIN.name())
                        .anyRequest()
                        .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
