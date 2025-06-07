package com.bibengine.web;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Controller
public class AuthWebController {
    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String register() { return "register"; }

    /**
     * Obsługa formularza rejestracji. Wywołuje API serwera i w razie sukcesu
     * przekierowuje do strony logowania. W przypadku błędu zwraca stronę
     * rejestracji z komunikatem.
     */
    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 Model model) {
        RestTemplate rest = new RestTemplate();
        var body = java.util.Map.of(
                "username", username,
                "email", email,
                "password", password
        );
        try {
            ResponseEntity<String> resp = rest.exchange(
                    "http://localhost:5100/api/auth/register",
                    HttpMethod.POST,
                    new HttpEntity<>(body),
                    String.class
            );
            if (resp.getStatusCode().is2xxSuccessful()) {
                return "redirect:/login";
            }
        } catch (RestClientException ex) {
            // ignorujemy szczegóły i wyświetlamy prosty komunikat
        }
        model.addAttribute("error", "Nie udało się zarejestrować użytkownika");
        return "register";
    }
}
