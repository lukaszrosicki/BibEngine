package com.bibengine.web;

import com.bibengine.web.dto.BibliographyDto;
import com.bibengine.web.dto.BibEntryDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Proste widoki do zarządzania bazą bibliograficzną.
 * Komunikuje się z API serwera za pomocą RestTemplate i tokenu JWT
 * przechowywanego w sesji.
 */
@Controller
@RequestMapping("/bibliography")
public class BibliographyWebController {
    private final RestTemplate rest = new RestTemplate();

    private HttpEntity<?> authEntity(HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        Object token = session.getAttribute("token");
        if (token != null) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return new HttpEntity<>(headers);
    }

    // Lista baz bibliograficznych
    @GetMapping
    public String list(Model model, HttpSession session) {
        try {
            ResponseEntity<BibliographyDto[]> resp = rest.exchange(
                    "http://localhost:5100/api/bibliography",
                    HttpMethod.GET,
                    authEntity(session),
                    BibliographyDto[].class
            );
            model.addAttribute("bibliographies", resp.getBody());
        } catch (Exception ex) {
            model.addAttribute("error", "Nie udało się pobrać danych");
        }
        return "bibliography";
    }

    // Dodawanie nowej bazy
    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String comment,
                         HttpSession session) {
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("comment", comment);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + session.getAttribute("token"));
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        try {
            rest.exchange("http://localhost:5100/api/bibliography",
                    HttpMethod.POST,
                    entity,
                    BibliographyDto.class);
        } catch (Exception ignored) {}
        return "redirect:/bibliography";
    }

    // Wyświetlanie wpisów konkretnej bazy
    @GetMapping("/{id}")
    public String entries(@PathVariable Long id, Model model, HttpSession session) {
        BibliographyDto bibliography = null;
        try {
            ResponseEntity<BibliographyDto[]> respBibs = rest.exchange(
                    "http://localhost:5100/api/bibliography",
                    HttpMethod.GET,
                    authEntity(session),
                    BibliographyDto[].class);
            if (respBibs.getBody() != null) {
                for (BibliographyDto b : respBibs.getBody()) {
                    if (b.id().equals(id)) { bibliography = b; break; }
                }
            }
            ResponseEntity<BibEntryDto[]> resp = rest.exchange(
                    "http://localhost:5100/api/bibliography/" + id + "/entries",
                    HttpMethod.GET,
                    authEntity(session),
                    BibEntryDto[].class);
            model.addAttribute("entries", resp.getBody());
        } catch (Exception ex) {
            model.addAttribute("error", "Błąd pobierania danych");
        }
        model.addAttribute("bibliography", bibliography);
        return "entries";
    }

    // Dodawanie wpisu przez formularz
    @PostMapping("/{id}/entries")
    public String addEntry(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam String authors,
                           @RequestParam(required = false) Integer year,
                           @RequestParam(required = false) String journal,
                           @RequestParam(required = false) String doi,
                           @RequestParam(required = false) String type,
                           HttpSession session) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("authors", authors);
        body.put("year", year);
        body.put("journal", journal);
        body.put("doi", doi);
        body.put("type", type);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + session.getAttribute("token"));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        try {
            rest.exchange("http://localhost:5100/api/bibliography/" + id + "/entries",
                    HttpMethod.POST, entity, BibEntryDto.class);
        } catch (Exception ignored) {}
        return "redirect:/bibliography/" + id;
    }

    // Dodawanie wpisu po DOI
    @PostMapping("/{id}/entries/by-doi")
    public String addByDoi(@PathVariable Long id,
                           @RequestParam String doi,
                           HttpSession session) {
        try {
            rest.exchange(
                    "http://localhost:5100/api/bibliography/" + id + "/entries/by-doi?doi=" + doi,
                    HttpMethod.GET,
                    authEntity(session),
                    BibEntryDto.class);
        } catch (Exception ignored) {}
        return "redirect:/bibliography/" + id;
    }
}
