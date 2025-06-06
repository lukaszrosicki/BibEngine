package com.bibengine.security;

import com.bibengine.user.User;
import com.bibengine.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        // prosta walidacja maila
        if (!request.email().contains("@")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.register(request.username(), request.email(), request.password()));
    }

    // TODO: logowanie i JWT
}

record RegisterRequest(String username, String email, String password) {}
