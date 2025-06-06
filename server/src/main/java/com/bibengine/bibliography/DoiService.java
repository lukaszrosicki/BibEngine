package com.bibengine.bibliography;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/** Proste pobieranie metadanych po DOI poprzez API Crossref */
@Service
public class DoiService {
    private final RestTemplate rest = new RestTemplate();

    public BibEntry fetchByDoi(String doi) {
        String url = "https://api.crossref.org/works/" + doi;
        Map response = rest.getForObject(url, Map.class);
        if (response == null) return null;
        Map<String, Object> message = (Map<String, Object>) response.get("message");
        BibEntry entry = new BibEntry();
        entry.setTitle(((java.util.List<String>) message.getOrDefault("title", java.util.List.of(""))).get(0));
        entry.setAuthors(((java.util.List<Map<String, Object>>) message.getOrDefault("author", java.util.List.of()))
                .stream().map(a -> a.get("family") + " " + a.get("given")).reduce((a,b) -> a + ", " + b).orElse(""));
        if (message.get("issued") instanceof Map i && ((Map) i).get("date-parts") instanceof java.util.List list && !list.isEmpty()) {
            entry.setYear((Integer) ((java.util.List) ((java.util.List) list).get(0)).get(0));
        }
        entry.setJournal((String) message.getOrDefault("container-title", ""));
        entry.setDoi(doi);
        entry.setType((String) message.getOrDefault("type", "article"));
        return entry;
    }
}
