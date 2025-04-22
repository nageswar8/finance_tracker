package com.finance.tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/success")
    public ResponseEntity<?> loginSuccess(OAuth2AuthenticationToken token) {
        Map<String, Object> attributes = token.getPrincipal().getAttributes();

        Map<String, String> response = Map.of(
                "email", (String) attributes.get("email"),
                "name", (String) attributes.get("name")
        );

        return ResponseEntity.ok(response);

    }
}
