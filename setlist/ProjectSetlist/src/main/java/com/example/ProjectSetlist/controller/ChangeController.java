package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Setlist;
import com.example.ProjectSetlist.model.Change;
import com.example.ProjectSetlist.repository.ChangeRepository;

import com.example.ProjectSetlist.repository.SetlistRepository;

import org.springframework.stereotype.Controller;


import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/changes")
public class ChangeController {

    private final ChangeRepository changeRepository;
    private final SetlistRepository setlistRepository;

    // Constructor untuk dependency injection
    public ChangeController(ChangeRepository changeRepository, SetlistRepository setlistRepository) {
        this.changeRepository = changeRepository;
        this.setlistRepository = setlistRepository;
    }

    @PostMapping("/{changeId}/approve")
    public String approveChange(@PathVariable Long changeId) {
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new IllegalArgumentException("Change not found"));
        
        System.out.println("Proposed songs for change ID " + changeId + ":");
        if (change.getProposedSongs() != null && !change.getProposedSongs().isEmpty()) {
            change.getProposedSongs().forEach(song -> System.out.println("- " + song));
        } else {
            System.out.println("No proposed songs.");
        }
        
        Setlist setlist = change.getSetlist();

        setlist.getSongs().clear();
        setlist.getSongs().addAll(change.getProposedSongs()); 
        
        System.out.println("Updated setlist songs: " + setlist.getSongs());
        
        setlistRepository.save(setlist);  
        
        // Update status change
        change.setStatus("Approved");
        changeRepository.save(change);  
        
        return "redirect:/shows/" + setlist.getShow().getId();
    }
    
    
    @PostMapping("/{changeId}/reject")
    public String rejectChange(@PathVariable Long changeId) {
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new IllegalArgumentException("Change not found"));
    
        Setlist setlist = change.getSetlist();
        if (setlist == null) {
            throw new IllegalStateException("Setlist not found for the given change.");
        }
    
        change.setStatus("Rejected");
        changeRepository.save(change);

        return "redirect:/shows/" + setlist.getShow().getId();
    }
    
}
