package com.bibengine.user;

import com.bibengine.bibliography.BibliographyRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BibliographyRepository bibliographyRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, BibliographyRepository bibliographyRepository) {
        this.userRepository = userRepository;
        this.bibliographyRepository = bibliographyRepository;
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

    /** Zmiana hasła bieżącego użytkownika */
    @Transactional
    public void changePassword(String username, String newPassword) {
        if (newPassword == null || newPassword.length() < 7) {
            throw new IllegalArgumentException("Hasło musi mieć min 7 znaków");
        }
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("Użytkownik nie istnieje");
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /** Aktualizacja loginu lub emaila */
    @Transactional
    public User updateProfile(String username, String newUsername, String newEmail) {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new IllegalArgumentException("Użytkownik nie istnieje");
        if (newUsername != null && !newUsername.equals(username)) {
            if (userRepository.existsByUsername(newUsername)) {
                throw new IllegalArgumentException("Użytkownik o tym loginie już istnieje");
            }
            user.setUsername(newUsername);
        }
        if (newEmail != null) {
            user.setEmail(newEmail);
        }
        return userRepository.save(user);
    }

    /** Usunięcie konta wraz z bibliografiami */
    @Transactional
    public void deleteAccount(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) return;
        var bibliographies = bibliographyRepository.findByOwner(user);
        bibliographyRepository.deleteAll(bibliographies);
        userRepository.delete(user);
    }
}
