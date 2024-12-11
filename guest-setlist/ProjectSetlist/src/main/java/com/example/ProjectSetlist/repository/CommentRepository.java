package com.example.ProjectSetlist.repository;

import com.example.ProjectSetlist.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySetlistIdOrderByCreatedAtAsc(Long setlistId);
}