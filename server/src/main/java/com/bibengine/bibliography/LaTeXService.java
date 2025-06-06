package com.bibengine.bibliography;

import org.springframework.stereotype.Service;

import java.util.List;

/** Generuje \thebibliography w stylu uproszczonym APA */
@Service
public class LaTeXService {
    public String generateTheBibliography(List<BibEntry> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("\\begin{thebibliography}{99}\n");
        int i = 1;
        for (BibEntry e : entries) {
            sb.append("\\bibitem{" + i++ + "} ");
            sb.append(e.getAuthors() + " (" + e.getYear() + "). ");
            sb.append(e.getTitle() + ". ");
            if (e.getJournal() != null) sb.append(e.getJournal() + ". ");
            if (e.getDoi() != null) sb.append("doi:" + e.getDoi());
            sb.append("\n");
        }
        sb.append("\\end{thebibliography}\n");
        return sb.toString();
    }
}
