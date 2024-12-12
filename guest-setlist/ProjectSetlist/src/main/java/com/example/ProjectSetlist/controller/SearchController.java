package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Show;
import com.example.ProjectSetlist.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private ShowRepository showRepository;


    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        // Cari semua show yang sesuai
        List<Show> shows = showRepository.searchShows(query);
    
        // Tambahkan hasil pencarian ke model
        model.addAttribute("shows", shows);
        model.addAttribute("query", query);
    
        return "search_results";
    }
}