package com.bibengine.bibliography;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.util.Comparator;
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
    public List<BibliographyDto> myBibliographies(Authentication auth,
                                                 @RequestParam(required = false) String q,
                                                 @RequestParam(required = false) String sort,
                                                 @RequestParam(required = false) String dir) {
        List<Bibliography> list = service.forUser(auth.getName());
        if (q != null && !q.isBlank()) {
            String ql = q.toLowerCase();
            list = list.stream().filter(b ->
                    (b.getName() != null && b.getName().toLowerCase().contains(ql)) ||
                            (b.getComment() != null && b.getComment().toLowerCase().contains(ql))
            ).toList();
        }
        if (sort != null) {
            Comparator<Bibliography> c = switch (sort) {
                case "comment" -> Comparator.comparing(Bibliography::getComment,
                        Comparator.nullsLast(String::compareToIgnoreCase));
                default -> Comparator.comparing(Bibliography::getName,
                        Comparator.nullsLast(String::compareToIgnoreCase));
            };
            if ("desc".equalsIgnoreCase(dir)) c = c.reversed();
            list = list.stream().sorted(c).toList();
        }
        return list.stream()
                .map(b -> new BibliographyDto(b.getId(), b.getName(), b.getComment(), service.countEntries(b.getId())))
                .toList();
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
    public List<BibEntry> listEntries(@PathVariable Long id,
                                      @RequestParam(required = false) String q,
                                      @RequestParam(required = false) String sort,
                                      @RequestParam(required = false) String dir) {
        Sort s = Sort.unsorted();
        if (sort != null) {
            Sort.Direction d = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            s = Sort.by(d, sort);
        }
        List<BibEntry> list = service.entries(id, s);
        if (q != null && !q.isBlank()) {
            String ql = q.toLowerCase();
            list = list.stream().filter(e ->
                    (e.getTitle() != null && e.getTitle().toLowerCase().contains(ql)) ||
                            (e.getAuthors() != null && e.getAuthors().toLowerCase().contains(ql)) ||
                            (e.getJournal() != null && e.getJournal().toLowerCase().contains(ql)) ||
                            (e.getYear() != null && e.getYear().toString().contains(ql))
            ).toList();
        }
        return list;
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
