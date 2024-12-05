package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Show;
import com.example.ProjectSetlist.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private ShowRepository showRepository;

    @GetMapping("/search")
    public String searchShows(@RequestParam("query") String query, Model model) {
        // Search for shows based on artist name, city, country, or venue
        List<Show> shows = showRepository.searchShows(query);
        model.addAttribute("shows", shows);
        model.addAttribute("query", query);
        return "search_results";
    }
}
