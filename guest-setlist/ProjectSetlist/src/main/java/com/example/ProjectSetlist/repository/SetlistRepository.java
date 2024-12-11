package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Setlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SetlistRepository extends JpaRepository<Setlist, Long> {
    Setlist findByShowId(Long showId);
    
    @Query("SELECT s FROM Setlist s JOIN s.songs song WHERE LOWER(song) LIKE LOWER(CONCAT('%', :song, '%'))")
    List<Setlist> findBySongContaining(String song);
}