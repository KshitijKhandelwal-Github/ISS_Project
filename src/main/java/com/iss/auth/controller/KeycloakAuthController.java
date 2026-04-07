package com.iss.auth.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@ConditionalOnProperty(name = "app.security.mode", havingValue = "keycloak")
public class KeycloakAuthController {

    @GetMapping("/login")
    public ResponseEntity<Void> redirectToKeycloak() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/oauth2/authorization/keycloak");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/logout/success")
    public ResponseEntity<String> logoutSuccess() {
        return ResponseEntity.ok("Keycloak logout completed");
    }
}
