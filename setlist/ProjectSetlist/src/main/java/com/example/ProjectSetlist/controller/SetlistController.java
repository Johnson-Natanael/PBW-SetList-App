package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Setlist;
import com.example.ProjectSetlist.model.Show;
import com.example.ProjectSetlist.model.Comment;
import com.example.ProjectSetlist.model.User;
import com.example.ProjectSetlist.repository.ChangeRepository;
import com.example.ProjectSetlist.repository.CommentRepository;
import com.example.ProjectSetlist.repository.SetlistRepository;
import com.example.ProjectSetlist.repository.ShowRepository;
import com.example.ProjectSetlist.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/setlists")
public class SetlistController {

    @Autowired
    private SetlistRepository setlistRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChangeRepository changeRepository;

    @GetMapping("/add")
    public String showAddSetlistForm(@RequestParam("showId") Long showId, Model model) {
        Show show = showRepository.findById(showId).orElse(null);
        if (show == null) {
            return "redirect:/shows";
        }
        Setlist setlist = new Setlist();
        setlist.setShow(show);
        model.addAttribute("setlist", setlist);
        return "setlist_add";
    }

    @PostMapping("/add")
    public String addSetlist(@Valid @ModelAttribute("setlist") Setlist setlist, BindingResult result, Model model) {
        if (setlist.getShow() != null && setlist.getShow().getId() != null) {
            Show show = showRepository.findById(setlist.getShow().getId()).orElse(null);
            if (show == null) {
                result.rejectValue("show", "error.setlist", "Invalid show");
                return "setlist_add";
            }
            setlist.setShow(show);
        } else {
            result.rejectValue("show", "error.setlist", "Show is required");
            return "setlist_add";
        }

        if (setlist.getSongsAsString() != null && !setlist.getSongsAsString().trim().isEmpty()) {
            List<String> songs = Arrays.asList(setlist.getSongsAsString().split("\\r?\\n"));
            setlist.setSongs(songs);
        } else {
            result.rejectValue("songsAsString", "error.setlist", "Songs are required");
            return "setlist_add";
        }

        if (setlist.getSongs().isEmpty()) {
            result.rejectValue("songsAsString", "error.setlist", "At least one song is required");
            return "setlist_add";
        }

        if (setlist.getProofFile() != null && !setlist.getProofFile().isEmpty()) {
            try {
                String filename = saveProof(setlist.getProofFile());
                setlist.setProofFilename(filename);
            } catch (IOException e) {
                result.rejectValue("proofFile", "error.setlist", e.getMessage());
                return "setlist_add";
            }
        }

        setlistRepository.save(setlist);
        return "redirect:/shows/" + setlist.getShow().getId();
    }

    @GetMapping("/{id}")
    public String viewSetlist(@PathVariable("id") Long id, Model model, Authentication authentication) {
        Setlist setlist = setlistRepository.findById(id).orElse(null);
        if (setlist == null) {
            return "redirect:/shows";
        }

        List<Comment> comments = commentRepository.findBySetlistIdOrderByCreatedAtAsc(id);
        model.addAttribute("setlist", setlist);
        model.addAttribute("comments", comments);

        if (authentication != null) {
            model.addAttribute("newComment", new Comment());
        }

        return "setlist_detail";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable("id") Long setlistId,
                             Authentication authentication,
                             @RequestParam String content,
                             Model model) {
        // Retrieve Setlist
        Setlist setlist = setlistRepository.findById(setlistId)
            .orElseThrow(() -> new IllegalArgumentException("Setlist not found"));
        System.out.println("Setlist found: " + setlist);
    
        // Retrieve User
        User currentUser = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println("User found: " + currentUser);
    
        // Initialize Comment Object
        Comment newComment = new Comment();
        newComment.setContent(content);
        newComment.setSetlist(setlist);
        newComment.setUser(currentUser);
        newComment.setCreatedAt(LocalDateTime.now());
        System.out.println("New comment: " + newComment);
    
        // Save the Comment
        commentRepository.save(newComment);
        System.out.println("Saved comment: " + newComment);
    
        return "redirect:/setlists/" + setlistId;
    }

    private String saveProof(MultipartFile proofFile) throws IOException {
        String uploadsDir = "uploads/proofs/";
        String originalFilename = proofFile.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalFilename;

        String contentType = proofFile.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new IOException("Invalid file type. Only images and PDFs are allowed.");
        }

        if (proofFile.getSize() > 10 * 1024 * 1024) {
            throw new IOException("File size exceeds limit of 10MB.");
        }

        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(proofFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }
}