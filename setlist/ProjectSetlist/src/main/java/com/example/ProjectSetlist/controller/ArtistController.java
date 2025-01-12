package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Artist;
import com.example.ProjectSetlist.repository.ArtistRepository;

import jakarta.validation.Valid;


import com.example.ProjectSetlist.model.Artist;
import com.example.ProjectSetlist.repository.ArtistRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/artists")
public class ArtistController {

    @Autowired
    private ArtistRepository artistRepository;

    @GetMapping
    public String listArtists(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size,
        Model model) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Artist> artistPage = artistRepository.findAll(pageable);

    model.addAttribute("artists", artistPage.getContent());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", artistPage.getTotalPages());
    return "artist_list";
}

    @GetMapping("/{id}")
    public String viewArtist(@PathVariable("id") Long id, Model model) {
        Artist artist = artistRepository.findById(id).orElse(null);
        if (artist == null) {
            return "redirect:/artists";
        }
        model.addAttribute("artist", artist);
        return "artist_detail";
    }

    @GetMapping("/add")
    public String showAddArtistForm(Model model) {
        model.addAttribute("artist", new Artist());
        return "artist_add";
    }


    @PostMapping("/add")
    public String addArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println("Errors occurred:");
            result.getAllErrors().forEach(error -> {
                System.out.println("Object: " + error.getObjectName());
                System.out.println("Code: " + error.getCode());
                System.out.println("Message: " + error.getDefaultMessage());
            });
            return "artist_add";
        }
        try {
            // Handle file upload
            if (artist.getImageFile() != null && !artist.getImageFile().isEmpty()) {
                String filename = saveImage(artist.getImageFile());
                artist.setImageFilename(filename);
            }
        } catch (IOException e) {
            result.rejectValue("imageFile", "error.artist", e.getMessage());
            return "artist_add";
        }
    
        artistRepository.save(artist);
        return "redirect:/artists";
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        String uploadsDir = "uploads/artists/";
        String originalFilename = imageFile.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalFilename;
    
        // Validate file type
        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("Invalid file type. Only image files are allowed.");
        }
    
        // Validate file extension
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList("jpg", "jpeg", "png").contains(fileExtension)) {
            throw new IOException("Invalid file extension. Only JPG and PNG files are allowed.");
        }
    
        // Limit file size
        if (imageFile.getSize() > 5 * 1024 * 1024) {
            throw new IOException("File size exceeds limit of 5MB.");
        }
    
        // Ensure the uploads directory exists
        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    
        // Save the file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
    
        return filename;
    }
}