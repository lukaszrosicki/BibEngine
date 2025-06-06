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

    public Optional<Bibliography> get(Long id) { return bibliographyRepository.findById(id); }

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

    public BibEntry addEntry(Long bibliographyId, BibEntry entry) {
        Bibliography b = bibliographyRepository.findById(bibliographyId).orElseThrow();
        entry.setBibliography(b);
        return bibEntryRepository.save(entry);
    }
}
