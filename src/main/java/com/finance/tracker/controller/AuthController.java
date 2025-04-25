package com.finance.tracker.controller;

import com.finance.tracker.entity.User;
import com.finance.tracker.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();

        Map<String, String> response = Map.of(
                "email", (String) attributes.get("email"),
                "name", (String) attributes.get("name")
        );

        return ResponseEntity.ok(response);


    }

    @Operation(summary = "Get current user", description = "Returns the currently logged-in user's details.")
    @GetMapping("/api/me")
    public ResponseEntity<User> getCurrentUser(OAuth2AuthenticationToken auth) {
        String email = auth.getPrincipal().getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow();
        System.out.println(user);
        return ResponseEntity.ok(user);
    }

}
