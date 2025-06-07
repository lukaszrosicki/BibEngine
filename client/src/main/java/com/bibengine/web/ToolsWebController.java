package com.bibengine.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/** Widoki narzędziowe np. czyszczenie pliku BibTeX */
@Controller
@RequestMapping("/tools")
public class ToolsWebController {
    private final RestTemplate rest = new RestTemplate();

    private HttpHeaders authHeaders(HttpSession session, MediaType type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(type);
        Object token = session.getAttribute("token");
        if (token != null) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        return headers;
    }

    @GetMapping("/clean-bibtex")
    public String cleanForm() {
        return "clean-bibtex";
    }

    @PostMapping(value = "/clean-bibtex")
    public String clean(@RequestParam("file") MultipartFile file, Model model, HttpSession session) {
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() { return file.getOriginalFilename(); }
            };
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, authHeaders(session, MediaType.MULTIPART_FORM_DATA));
            ResponseEntity<Map> resp = rest.exchange("http://localhost:5100/api/bibliography/clean", HttpMethod.POST, entity, Map.class);
            model.addAttribute("bibtex", resp.getBody().get("bibtex"));
            model.addAttribute("latex", resp.getBody().get("latex"));
        } catch (Exception ex) {
            model.addAttribute("error", "Nie udało się przetworzyć pliku");
        }
        return "clean-bibtex";
    }
}
