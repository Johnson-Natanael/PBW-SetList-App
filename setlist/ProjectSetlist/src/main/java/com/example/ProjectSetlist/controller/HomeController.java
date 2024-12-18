package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Show;
import com.example.ProjectSetlist.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ShowRepository showRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Membatasi hanya 3 show terbaru
        Pageable pageable = PageRequest.of(0, 3);  // Ambil hanya 3 entitas
        List<Show> latestShows = showRepository.findTop3ByOrderByIdDesc(pageable);
        
        // Mengirimkan data show ke template
        model.addAttribute("latestShows", latestShows);
        
        return "home";
    }

}
