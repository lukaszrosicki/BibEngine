package com.bibengine.bibliography;

/** Prosty DTO z dodatkową informacją o liczbie wpisów */
public record BibliographyDto(Long id, String name, String comment, int entryCount) {}
