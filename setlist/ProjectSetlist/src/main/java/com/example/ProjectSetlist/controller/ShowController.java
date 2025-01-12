package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Artist;
import com.example.ProjectSetlist.model.Comment;
import com.example.ProjectSetlist.model.Setlist;
import com.example.ProjectSetlist.model.Show;
import com.example.ProjectSetlist.model.User;
import com.example.ProjectSetlist.repository.ArtistRepository;
import com.example.ProjectSetlist.repository.ChangeRepository;
import com.example.ProjectSetlist.repository.CommentRepository;
import com.example.ProjectSetlist.repository.SetlistRepository;
import com.example.ProjectSetlist.repository.ShowRepository;
import com.example.ProjectSetlist.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.nio.file.*;

@Controller
@RequestMapping("/shows")
public class ShowController {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SetlistRepository setlistRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/add")
    public String showAddShowForm(@RequestParam("artistId") Long artistId, Model model) {
        Artist artist = artistRepository.findById(artistId).orElse(null);
        if (artist == null) {
            return "redirect:/artists";
        }
        Show show = new Show();
        show.setArtist(artist);
        model.addAttribute("show", show);
        return "show_add";
    }

    @PostMapping("/add")
    public String addShow(@Valid @ModelAttribute("show") Show show, BindingResult result, @RequestParam("artistId") Long artistId, Model model) {
        if (result.hasErrors()) {
            System.out.println("Errors occurred:");
            result.getAllErrors().forEach(error -> {
                System.out.println("Object: " + error.getObjectName());
                System.out.println("Code: " + error.getCode());
                System.out.println("Message: " + error.getDefaultMessage());
            });
    
            Artist artist = artistRepository.findById(artistId).orElse(null);
            show.setArtist(artist);
            model.addAttribute("show", show);
            return "show_add";
        }
        showRepository.save(show);
        return "redirect:/artists/" + show.getArtist().getId();
    }
    
    @GetMapping("/{id}")
    public String viewShow(@PathVariable("id") Long id, Model model) {
        Show show = showRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Show not found"));
    
        // Ambil Setlist yang terkait
        Setlist setlist = show.getSetlist();
    
        // Ambil komentar dari Setlist
        List<Comment> comments = (setlist != null) ? commentRepository.findBySetlistIdOrderByCreatedAtAsc(setlist.getId()) : List.of();
    
        model.addAttribute("show", show);
        model.addAttribute("setlist", setlist);
        model.addAttribute("comments", comments);
        return "show_detail";
    }    

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable("id") Long showId,
                             Authentication authentication,
                             @RequestParam String content) {
        // Cari Show berdasarkan ID
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new IllegalArgumentException("Show not found"));
    
        // Ambil Setlist dari Show
        Setlist setlist = show.getSetlist();
        if (setlist == null) {
            throw new IllegalArgumentException("Setlist not found for this Show");
        }
    
        // Cari User berdasarkan authentication
        User currentUser = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
        // Buat Comment baru
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setSetlist(setlist); // Hubungkan komentar ke Setlist
        newComment.setUser(currentUser);
        newComment.setCreatedAt(LocalDateTime.now());
    
        // Simpan Comment
        commentRepository.save(newComment);
    
        return "redirect:/shows/" + showId; // Kembali ke halaman Show
    }
    
}