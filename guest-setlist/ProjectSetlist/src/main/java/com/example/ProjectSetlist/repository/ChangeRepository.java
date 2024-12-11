package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Change;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeRepository extends JpaRepository<Change, Long> {
    List<Change> findBySetlistIdOrderByTimestampAsc(Long setlistId);
}