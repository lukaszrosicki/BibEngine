package com.bibengine.bibliography;

import org.springframework.stereotype.Service;

/** Generuje prosty wpis BibTeX zawierający najważniejsze pola */
@Service
public class BibTexService {
    public String toBibtex(BibEntry entry) {
        String id = entry.getAuthors().split(" ")[0] + entry.getYear();
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
}
