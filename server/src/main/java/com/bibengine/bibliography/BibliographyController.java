package com.bibengine.bibliography;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Kontroler REST do zarządzania bazami bibliograficznymi */
@RestController
@RequestMapping("/api/bibliography")
public class BibliographyController {
    private final BibliographyService service;
    private final DoiService doiService;
    private final BibTexService bibTexService;
    private final LaTeXService laTeXService;

    public BibliographyController(BibliographyService service, DoiService doiService,
                                 BibTexService bibTexService, LaTeXService laTeXService) {
        this.service = service;
        this.doiService = doiService;
        this.bibTexService = bibTexService;
        this.laTeXService = laTeXService;
    }

    @GetMapping
    public List<Bibliography> myBibliographies(Authentication auth) {
        return service.forUser(auth.getName());
    }

    @PostMapping
    public Bibliography create(Authentication auth, @RequestBody Bibliography b) {
        return service.create(auth.getName(), b);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }

    @PostMapping("/{id}/entries")
    public BibEntry addEntry(@PathVariable Long id, @RequestBody BibEntry entry) {
        return service.addEntry(id, entry);
    }

    @GetMapping("/{id}/entries")
    public List<BibEntry> listEntries(@PathVariable Long id) {
        return service.get(id).map(Bibliography::getEntries).orElse(List.of());
    }

    @GetMapping("/{id}/entries/by-doi")
    public BibEntry addByDoi(@PathVariable Long id, @RequestParam String doi) {
        // pobieranie wpisu z Crossref po DOI
        BibEntry entry = doiService.fetchByDoi(doi);
        return service.addEntry(id, entry);
    }

    @GetMapping("/{id}/bibtex")
    public String bibtex(@PathVariable Long id) {
        return service.get(id)
                .map(b -> b.getEntries().stream().map(bibTexService::toBibtex).reduce("", String::concat))
                .orElse("");
    }

    @GetMapping("/{id}/latex")
    public String latex(@PathVariable Long id) {
        return service.get(id).map(b -> laTeXService.generateTheBibliography(b.getEntries()))
                .orElse("");
    }

    @GetMapping("/entries/search")
    public List<BibEntry> search(Authentication auth, @RequestParam String q) {
        // wyszukiwanie we wszystkich wpisach uzytkownika
        return service.searchEntries(auth.getName(), q);
    }
}
