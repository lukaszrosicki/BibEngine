package com.bibengine.bibliography;

import com.bibengine.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BibliographyRepository extends JpaRepository<Bibliography, Long> {
    List<Bibliography> findByOwner(User owner);
}
