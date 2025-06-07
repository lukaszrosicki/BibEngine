package com.bibengine.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Widok czyszczenia pliku BibTeX i generowania \thebibliography.
 */
@Controller
public class BibTexWebController {
    private final RestTemplate rest = new RestTemplate();

    private HttpEntity<?> authEntity(HttpSession session, HttpEntity.BodyBuilder builder) {
        HttpHeaders headers = new HttpHeaders();
        Object token = session.getAttribute("token");
        if (token != null) headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return builder.headers(headers).build();
    }

    @GetMapping("/bibtex-clean")
    public String form() { return "bibtex_clean"; }

    @PostMapping("/bibtex-clean")
    public String handle(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultipartFile> entity = new HttpEntity<>(file, headers);
            ResponseEntity<Map> resp = rest.exchange(
                    "http://localhost:5100/api/bibtex/clean",
                    HttpMethod.POST,
                    entity,
                    Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                model.addAttribute("bibtex", resp.getBody().get("bibtex"));
                model.addAttribute("latex", resp.getBody().get("latex"));
            }
        } catch (Exception ex) {
            model.addAttribute("error", "Nie udało się przetworzyć pliku");
        }
        return "bibtex_clean";
    }
}
