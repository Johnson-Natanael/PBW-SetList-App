package com.example.ProjectSetlist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "shows")
@Getter
@Setter
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Artist is required")
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Venue is required")
    private String venue;

    private String city;
    private String country;

    // Reference to the associated setlist
    @OneToOne(mappedBy = "show", cascade = CascadeType.ALL)
    private Setlist setlist;
}
