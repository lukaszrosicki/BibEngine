package com.bibengine.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Kontroler pozwalający użytkownikowi zarządzać swoim kontem
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/password")
    public void changePassword(Authentication auth, @RequestBody Map<String, String> body) {
        userService.changePassword(auth.getName(), body.get("password"));
    }

    @PutMapping
    public User update(Authentication auth, @RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        return userService.updateProfile(auth.getName(), username, email);
    }

    @DeleteMapping
    public void delete(Authentication auth) {
        userService.deleteAccount(auth.getName());
    }
}
