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
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Użytkownik o tym loginie już istnieje");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }

    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) return false;
        return passwordEncoder.matches(password, user.getPassword());
    }
}
