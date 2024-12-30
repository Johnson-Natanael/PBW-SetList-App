package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChangeRepository extends JpaRepository<Change, Long> {
    List<Change> findBySetlistIdOrderByTimestampAsc(Long setlistId);

    // Mengambil perubahan berdasarkan setlist_id dan status
    List<Change> findBySetlistIdAndStatus(Long setlistId, String status);

    // Alternatif menggunakan Query JPA
    @Query("SELECT c FROM Change c WHERE c.setlist.id = :setlistId AND c.status = :status")
    List<Change> findChangesBySetlistIdAndStatus(@Param("setlistId") Long setlistId, @Param("status") String status);
}