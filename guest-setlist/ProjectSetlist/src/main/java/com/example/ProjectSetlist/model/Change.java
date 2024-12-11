package com.example.ProjectSetlist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "changes")
@Getter
@Setter
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The setlist this change is associated with
    @NotNull
    @ManyToOne
    @JoinColumn(name = "setlist_id", nullable = false)
    private Setlist setlist;

    // The user who suggested the change
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Change description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Field to store the proposed new songs as a list
    @ElementCollection
    @CollectionTable(name = "change_songs", joinColumns = @JoinColumn(name = "change_id"))
    @Column(name = "song")
    private List<String> proposedSongs;

    // Status of the change (e.g., pending, approved, rejected)
    @Column(nullable = false)
    private String status;
}