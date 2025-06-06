package com.bibengine.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // prosta rejestracja użytkownika
    public User register(String username, String email, String password) {
        if (password.length() < 7) {
            throw new IllegalArgumentException("Hasło musi mieć min 7 znaków");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        return userRepository.save(user);
    }
}
