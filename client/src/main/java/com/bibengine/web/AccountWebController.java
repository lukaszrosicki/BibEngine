package com.bibengine.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Proste widoki do zarządzania kontem użytkownika
 */
@Controller
@RequestMapping("/account")
public class AccountWebController {
    private final RestTemplate rest = new RestTemplate();

    private HttpHeaders authHeaders(HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Object token = session.getAttribute("token");
        if (token != null) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return headers;
    }

    private HttpEntity<?> authEntity(HttpSession session) {
        return new HttpEntity<>(authHeaders(session));
    }

    @GetMapping
    public String account(Model model, @RequestParam(required = false) String error) {
        if (error != null) model.addAttribute("error", error);
        return "account";
    }

    @PostMapping("/update")
    public String update(@RequestParam String username,
                         @RequestParam String email,
                         HttpSession session,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Map<String, String> body = Map.of("username", username, "email", email);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, authHeaders(session));
        try {
            rest.exchange("http://localhost:5100/api/account", HttpMethod.PUT, entity, String.class);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Nie udało się zaktualizować profilu");
        }
        return "redirect:/account";
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String password,
                                 HttpSession session,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes ra) {
        Map<String, String> body = Map.of("password", password);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, authHeaders(session));
        try {
            rest.exchange("http://localhost:5100/api/account/password", HttpMethod.PUT, entity, String.class);
        } catch (Exception ex) {
            ra.addFlashAttribute("error", "Nie udało się zmienić hasła");
        }
        return "redirect:/account";
    }

    @PostMapping("/delete")
    public String delete(HttpSession session) {
        try {
            rest.exchange("http://localhost:5100/api/account", HttpMethod.DELETE, authEntity(session), String.class);
        } catch (Exception ignored) {}
        session.invalidate();
        return "redirect:/";
    }
}
