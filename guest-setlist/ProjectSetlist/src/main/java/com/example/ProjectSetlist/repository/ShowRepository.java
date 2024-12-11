package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Artist;
import com.example.ProjectSetlist.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ShowRepository extends JpaRepository<Show, Long> {
    @Query("SELECT s FROM Show s " +
        "JOIN s.artist a " +
        "WHERE LOWER(s.venue) LIKE LOWER(CONCAT('%', :query, '%')) " +
        "   OR LOWER(s.city) LIKE LOWER(CONCAT('%', :query, '%')) " +
        "   OR LOWER(s.country) LIKE LOWER(CONCAT('%', :query, '%')) " +
        "   OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Show> searchShows(@Param("query") String query);

    Optional<Show> findByArtistAndVenue(Artist artist, String venue);

}
