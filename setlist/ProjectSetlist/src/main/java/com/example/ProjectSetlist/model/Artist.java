package com.example.ProjectSetlist.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "artists")
@Getter
@Setter
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false) 
    private String imageFilename;

    @Transient
    private MultipartFile imageFile;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    private List<Show> shows;
}
