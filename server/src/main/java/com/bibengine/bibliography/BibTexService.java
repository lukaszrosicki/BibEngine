package com.bibengine.bibliography;

import org.springframework.stereotype.Service;

/** Generuje prosty wpis BibTeX zawierający najważniejsze pola */
@Service
public class BibTexService {
    public String toBibtex(BibEntry entry) {
        String id = entry.getBibtexKey() != null ? entry.getBibtexKey() :
                entry.getAuthors().split(" ")[0] + entry.getYear();
        StringBuilder sb = new StringBuilder();
        sb.append("@" + entry.getType() + "{" + id + ",\n");
        sb.append("  title={'" + entry.getTitle() + "'},\n");
        sb.append("  author={'" + entry.getAuthors() + "'},\n");
        if (entry.getJournal() != null) sb.append("  journal={'" + entry.getJournal() + "'},\n");
        if (entry.getYear() != null) sb.append("  year={'" + entry.getYear() + "'},\n");
        if (entry.getDoi() != null) sb.append("  doi={'" + entry.getDoi() + "'},\n");
        sb.append("}\n");
        return sb.toString();
    }

    /** Prosty parser BibTeX zwracający listę wpisów */
    public java.util.List<BibEntry> fromBibtex(String text) {
        java.util.List<BibEntry> list = new java.util.ArrayList<>();
        String[] parts = text.split("@");
        for (String p : parts) {
            p = p.trim();
            if (p.isEmpty()) continue;
            int brace = p.indexOf('{');
            int comma = p.indexOf(',', brace);
            if (brace < 0 || comma < 0) continue;
            String type = p.substring(0, brace).trim();
            String key = p.substring(brace + 1, comma).trim();
            String fields = p.substring(comma + 1);
            int end = fields.lastIndexOf('}');
            if (end >= 0) fields = fields.substring(0, end);
            BibEntry entry = new BibEntry();
            entry.setType(type);
            entry.setBibtexKey(key);
            for (String line : fields.split("\n")) {
                line = line.trim();
                if (line.endsWith(",")) line = line.substring(0, line.length()-1);
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String field = line.substring(0, eq).trim().toLowerCase();
                String value = line.substring(eq + 1).trim();
                if (value.startsWith("{") || value.startsWith("\""))
                    value = value.substring(1, value.length() - 1);
                switch (field) {
                    case "title" -> entry.setTitle(value);
                    case "author" -> entry.setAuthors(value);
                    case "journal", "booktitle" -> entry.setJournal(value);
                    case "year" -> {
                        try { entry.setYear(Integer.parseInt(value)); } catch (NumberFormatException ignored) {}
                    }
                    case "doi" -> entry.setDoi(value);
                }
            }
            list.add(entry);
        }
        return list;
    }
}
