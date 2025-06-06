package com.bibengine.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Zarządzanie użytkownikami - dostępne tylko dla roli ADMIN */
@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> list() { return userRepository.findAll(); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { userRepository.deleteById(id); }
}
