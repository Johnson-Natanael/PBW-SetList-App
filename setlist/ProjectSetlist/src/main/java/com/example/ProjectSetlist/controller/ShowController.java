package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Artist;
import com.example.ProjectSetlist.model.Setlist;
import com.example.ProjectSetlist.model.Show;
import com.example.ProjectSetlist.repository.ArtistRepository;
import com.example.ProjectSetlist.repository.SetlistRepository;
import com.example.ProjectSetlist.repository.ShowRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/shows")
public class ShowController {

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SetlistRepository setlistRepository;


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
    public String addShow(@Valid @ModelAttribute("show") Show show, BindingResult result) {
        if (result.hasErrors()) {
            return "show_add";  // Kembali ke form jika terdapat error
        }
        showRepository.save(show);
        return "redirect:/artists/" + show.getArtist().getId();
    }
    
    @GetMapping("/{id}")
    public String viewShow(@PathVariable("id") Long id, Model model) {
        Show show = showRepository.findById(id).orElse(null);
        if (show == null) {
            return "redirect:/"; // Redirect jika show tidak ditemukan
        }

        // Muat setlist terkait dengan show
        Setlist setlist = setlistRepository.findByShowId(id);
        model.addAttribute("show", show);
        model.addAttribute("setlist", setlist);
        return "show_detail";
    }
}