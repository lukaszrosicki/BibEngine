package com.bibengine.web.dto;

public record BibEntryDto(Long id, String title, String authors, Integer year,
                          String journal, String doi, String type) {}
