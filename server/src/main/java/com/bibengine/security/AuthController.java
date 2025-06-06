package com.bibengine.security;

import com.bibengine.user.User;
import com.bibengine.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        // prosta walidacja maila
        if (!request.email().contains("@")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.register(request.username(), request.email(), request.password()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (userService.authenticate(request.username(), request.password())) {
            String token = jwtService.generateToken(request.username());
            return ResponseEntity.ok(Map.of("token", token));
        }
        return ResponseEntity.status(401).build();
    }
}

record RegisterRequest(String username, String email, String password) {}
record LoginRequest(String username, String password) {}
