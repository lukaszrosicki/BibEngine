package com.bibengine.bibliography;

import jakarta.persistence.*;

/**
 * Wpis bibliograficzny zawierający tylko podstawowe pola wymagane w APA
 */
@Entity
public class BibEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String authors;

    @Column(name = "`year`")
    private Integer year;

    private String journal;

    private String doi;

    private String type; // np. article, book itd.

    @ManyToOne(fetch = FetchType.LAZY)
    private Bibliography bibliography;

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Bibliography getBibliography() { return bibliography; }
    public void setBibliography(Bibliography bibliography) { this.bibliography = bibliography; }
}
