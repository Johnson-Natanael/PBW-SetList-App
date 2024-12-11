package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByNameContainingIgnoreCase(String name);
    Optional<Artist> findByName(String name);

}