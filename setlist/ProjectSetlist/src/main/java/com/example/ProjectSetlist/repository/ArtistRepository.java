package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);
    Optional<Artist> findByName(String name);

    // Metode pencarian dengan pagination
    Page<Artist> findAll(Pageable pageable);

    // Metode pencarian berdasarkan nama dengan pagination
    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);
}