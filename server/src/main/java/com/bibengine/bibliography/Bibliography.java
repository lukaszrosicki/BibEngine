package com.bibengine.bibliography;

import com.bibengine.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Prosta encja reprezentująca listę bibliograficzną */
@Entity
public class Bibliography {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "bibliography", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BibEntry> entries = new ArrayList<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public List<BibEntry> getEntries() { return entries; }
}
