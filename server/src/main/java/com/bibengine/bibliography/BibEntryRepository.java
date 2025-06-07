package com.bibengine.bibliography;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BibEntryRepository extends JpaRepository<BibEntry, Long> {
    List<BibEntry> findByBibliographyOwnerId(Long ownerId);

    List<BibEntry> findByTitleContainingIgnoreCaseOrAuthorsContainingIgnoreCase(String title, String authors);

    long countByBibliographyId(Long bibliographyId);
}
