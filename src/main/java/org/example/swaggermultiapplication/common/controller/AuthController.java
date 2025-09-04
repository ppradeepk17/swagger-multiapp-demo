package org.example.swaggermultiapplication.common.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.swaggermultiapplication.common.security.JwtUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Auth Token Generation", description = "Auth Tokens for testing secured endpoints")
public class AuthController {

    @GetMapping("/auth/token")
    public Map<String, String> getToken(@RequestParam(defaultValue = "test-user") String username) {
        String token = JwtUtil.generateToken(username);
        return Map.of("token", token);
    }
}
