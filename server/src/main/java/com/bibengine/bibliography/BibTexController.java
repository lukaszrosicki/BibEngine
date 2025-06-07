package com.bibengine.bibliography;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bibtex")
public class BibTexController {
    private final BibTexCleanService cleanService;
    private final LaTeXService laTeXService;

    public BibTexController(BibTexCleanService cleanService, LaTeXService laTeXService) {
        this.cleanService = cleanService;
        this.laTeXService = laTeXService;
    }

    /**
     * Zwraca wyczyszczony tekst BibTeX oraz \thebibliography na podstawie przesłanego pliku.
     */
    @PostMapping("/clean")
    public Map<String, String> clean(@RequestParam("file") MultipartFile file) throws IOException {
        String text = new String(file.getBytes(), StandardCharsets.UTF_8);
        List<BibEntry> entries = cleanService.parse(text);
        String cleaned = cleanService.cleanToBibtex(entries);
        String latex = laTeXService.generateTheBibliography(entries);
        Map<String, String> result = new HashMap<>();
        result.put("bibtex", cleaned);
        result.put("latex", latex);
        return result;
    }
}
