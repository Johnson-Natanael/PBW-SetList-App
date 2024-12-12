package com.example.ProjectSetlist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Entity
@Table(name = "setlists")
@Getter
@Setter
public class Setlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    // Remove the @NotNull annotation here
    // @NotNull(message = "Songs are required")
    @ElementCollection
    @CollectionTable(name = "setlist_songs", joinColumns = @JoinColumn(name = "setlist_id"))
    @Column(name = "song")
    private List<String> songs;

    // // Transient field for songs input
    @Transient
    // @NotBlank(message = "Songs are required")
    private String songsAsString;

    // Field to store the filename of the uploaded proof
    private String proofFilename;

    // Transient field to handle the proof file upload
    @Transient
    private MultipartFile proofFile;
}