package com.bibengine.bibliography;

import com.bibengine.user.User;
import com.bibengine.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BibliographyService {
    private final BibliographyRepository bibliographyRepository;
    private final BibEntryRepository bibEntryRepository;
    private final UserRepository userRepository;

    public BibliographyService(BibliographyRepository bibliographyRepository,
                               BibEntryRepository bibEntryRepository,
                               UserRepository userRepository) {
        this.bibliographyRepository = bibliographyRepository;
        this.bibEntryRepository = bibEntryRepository;
        this.userRepository = userRepository;
    }

    public List<Bibliography> forUser(String username) {
        User user = userRepository.findByUsername(username);
        return bibliographyRepository.findByOwner(user);
    }

    @Transactional(readOnly = true)
    public Optional<Bibliography> get(Long id) {
        return bibliographyRepository.findById(id).map(b -> {
            b.getEntries().size(); // inicjalizacja wpisów w ramach transakcji
            return b;
        });
    }

    @Transactional
    public Bibliography create(String username, Bibliography b) {
        User owner = userRepository.findByUsername(username);
        b.setOwner(owner);
        return bibliographyRepository.save(b);
    }

    public void delete(Long id) { bibliographyRepository.deleteById(id); }

    public List<BibEntry> searchEntries(String username, String q) {
        // wyszukujemy wpisy użytkownika po tytule lub autorze
        User user = userRepository.findByUsername(username);
        return bibEntryRepository.findByTitleContainingIgnoreCaseOrAuthorsContainingIgnoreCase(q, q)
                .stream().filter(e -> e.getBibliography().getOwner().equals(user)).toList();
    }

    @Transactional
    public BibEntry addEntry(Long bibliographyId, BibEntry entry) {
        Bibliography b = bibliographyRepository.findById(bibliographyId).orElseThrow();
        entry.setBibliography(b);
        return bibEntryRepository.save(entry);
    }

    /** Aktualizuje istniejący wpis */
    @Transactional
    public BibEntry updateEntry(Long bibliographyId, Long entryId, BibEntry data) {
        BibEntry entry = bibEntryRepository.findById(entryId).orElseThrow();
        if (!entry.getBibliography().getId().equals(bibliographyId)) {
            throw new IllegalArgumentException("Entry does not belong to bibliography");
        }
        entry.setTitle(data.getTitle());
        entry.setAuthors(data.getAuthors());
        entry.setYear(data.getYear());
        entry.setJournal(data.getJournal());
        entry.setDoi(data.getDoi());
        entry.setType(data.getType());
        entry.setBibtexKey(data.getBibtexKey());
        return bibEntryRepository.save(entry);
    }

    /** Usuwa wpis z bibliografii */
    @Transactional
    public void deleteEntry(Long bibliographyId, Long entryId) {
        BibEntry entry = bibEntryRepository.findById(entryId).orElseThrow();
        if (!entry.getBibliography().getId().equals(bibliographyId)) {
            throw new IllegalArgumentException("Entry does not belong to bibliography");
        }
        bibEntryRepository.delete(entry);
    }

    /** Dodaje wiele wpisów jednocześnie */
    @Transactional
    public java.util.List<BibEntry> addEntries(Long bibliographyId, java.util.List<BibEntry> entries) {
        Bibliography b = bibliographyRepository.findById(bibliographyId).orElseThrow();
        for (BibEntry e : entries) { e.setBibliography(b); }
        return bibEntryRepository.saveAll(entries);
    }
}
