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
        // Ambil perubahan berdasarkan ID
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new IllegalArgumentException("Change not found"));
        
        // Debug: Log daftar lagu yang diusulkan
        System.out.println("Proposed songs for change ID " + changeId + ":");
        if (change.getProposedSongs() != null && !change.getProposedSongs().isEmpty()) {
            change.getProposedSongs().forEach(song -> System.out.println("- " + song));
        } else {
            System.out.println("No proposed songs.");
        }
        
        // Update setlist dengan lagu yang diusulkan
        Setlist setlist = change.getSetlist();

        setlist.getSongs().clear();  // Bersihkan lagu lama
        setlist.getSongs().addAll(change.getProposedSongs());  // Tambahkan lagu baru
        
        System.out.println("Updated setlist songs: " + setlist.getSongs());
        
        // Simpan setlist setelah update koleksi lagu
        setlistRepository.save(setlist);  // Memastikan perubahan disimpan
        
        // Update status change
        change.setStatus("Approved");
        changeRepository.save(change);  // Memastikan status perubahan disimpan
        
        return "redirect:/setlists/" + setlist.getId();
    }
    
    
    @PostMapping("/{changeId}/reject")
    public String rejectChange(@PathVariable Long changeId) {
        Change change = changeRepository.findById(changeId)
                .orElseThrow(() -> new IllegalArgumentException("Change not found"));

        // Update status change
        change.setStatus("Rejected");
        changeRepository.save(change);

        return "redirect:/setlists/" + change.getSetlist().getId();
    }
}
