package com.finance.tracker.config;

import com.finance.tracker.service.CustomOAuth2LoginSuccessHandler;
import com.finance.tracker.service.CustomOAuth2UserService;
import jakarta.servlet.annotation.WebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@WebFilter
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final CustomOAuth2LoginSuccessHandler successHandler;


    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomOAuth2LoginSuccessHandler successHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**", "/api/transactions/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Allow transactions
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)).successHandler(successHandler)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Frontend React URL
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Needed if using cookies/JWTs in frontend

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS to all endpoints
        return source;
    }


}
