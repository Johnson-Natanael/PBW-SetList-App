package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {

    @Query("""
           SELECT s 
           FROM Show s 
           WHERE LOWER(s.artist.name) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(s.city) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(s.country) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(s.venue) LIKE LOWER(CONCAT('%', :query, '%'))
           """)
    List<Show> searchShows(String query);
}
