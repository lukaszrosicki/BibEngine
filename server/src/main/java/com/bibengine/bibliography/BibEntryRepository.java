package com.bibengine.bibliography;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.domain.Sort;

public interface BibEntryRepository extends JpaRepository<BibEntry, Long> {
    List<BibEntry> findByBibliographyOwnerId(Long ownerId);

    List<BibEntry> findByTitleContainingIgnoreCaseOrAuthorsContainingIgnoreCase(String title, String authors);

    List<BibEntry> findByBibliographyId(Long bibliographyId, Sort sort);

    long countByBibliographyId(Long bibliographyId);
}
