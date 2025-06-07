package com.bibengine.bibliography;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Proste czyszczenie pliku BibTeX. Zachowuje tylko pola wymagane
 * dla podstawowych typów wpisów w APA (article oraz book).
 */
@Service
public class BibTexCleanService {

    /**
     * Parsuje tekst BibTeX do listy wpisów BibEntry.
     */
    public List<BibEntry> parse(String text) throws IOException {
        List<BibEntry> entries = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new StringReader(text));
        String line;
        StringBuilder current = null;
        while ((line = reader.readLine()) != null) {
            if (line.trim().startsWith("@")) {
                if (current != null) {
                    entries.add(parseEntry(current.toString()));
                }
                current = new StringBuilder();
                current.append(line).append('\n');
            } else if (current != null) {
                current.append(line).append('\n');
            }
        }
        if (current != null) {
            entries.add(parseEntry(current.toString()));
        }
        return entries;
    }

    private BibEntry parseEntry(String entryText) {
        BibEntry e = new BibEntry();
        // typ jest przed klamrą np. @article{key,
        int idx = entryText.indexOf('{');
        if (idx > 1) {
            e.setType(entryText.substring(1, idx).trim());
        }
        Pattern p = Pattern.compile("(\\w+)\\s*=\\s*[{\"](.*?)[}\"]", Pattern.DOTALL);
        Matcher m = p.matcher(entryText);
        while (m.find()) {
            String key = m.group(1).toLowerCase();
            String val = m.group(2).replaceAll("\n", " ").trim();
            switch (key) {
                case "title" -> e.setTitle(val);
                case "author" -> e.setAuthors(val);
                case "year" -> {
                    try { e.setYear(Integer.parseInt(val)); } catch (NumberFormatException ignored) {}
                }
                case "journal" -> e.setJournal(val);
                case "publisher" -> e.setJournal(val); // treat publisher same as journal field
                case "doi" -> e.setDoi(val);
            }
        }
        return e;
    }

    /**
     * Generuje wyczyszczony BibTeX zawierający tylko wymagane pola.
     */
    public String cleanToBibtex(List<BibEntry> entries) {
        StringBuilder sb = new StringBuilder();
        for (BibEntry e : entries) {
            sb.append(new BibTexService().toBibtex(e));
        }
        return sb.toString();
    }
}
