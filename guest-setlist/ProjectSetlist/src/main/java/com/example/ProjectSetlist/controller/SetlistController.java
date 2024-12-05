package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Setlist;
import com.example.ProjectSetlist.repository.SetlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SetlistController {

    @Autowired
    private SetlistRepository setlistRepository;

    @GetMapping("/setlist/{showId}")
    public String getSetlistByShowId(@PathVariable Long showId, Model model) {
        // Cari setlist berdasarkan ID show
        Setlist setlist = setlistRepository.findByShowId(showId);

        // Tambahkan setlist ke model
        if (setlist != null) {
            model.addAttribute("setlist", setlist);
        } else {
            model.addAttribute("errorMessage", "No setlist found for this show.");
        }

        return "setlist_detail";
    }
}
