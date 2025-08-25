package com.dietetic.backend.config;

import java.util.Arrays;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Usa la configuración de CORS de abajo
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // Rutas públicas
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://frontend-dietetics.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}