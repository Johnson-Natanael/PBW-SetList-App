package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Show;
import com.example.ProjectSetlist.repository.ShowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Autowired
    private ShowRepository showRepository;

    @GetMapping("/search/result")
    public String searchResult( @RequestParam("query") String query,
                                @RequestParam(value = "artist", required = false) String artist,
                                @RequestParam(value = "country", required = false) String country,
                                @RequestParam(value = "year", required = false) String year,
                               Model model) {
                                
        // Cari semua show berdasarkan query utama
        List<Show> shows = showRepository.searchShows(query);

        if (artist != null && !artist.isEmpty()) {
            shows = shows.stream()
                    .filter(show -> show.getArtist().getName().equalsIgnoreCase(artist))
                    .collect(Collectors.toList());
        }

        // Filter tambahan berdasarkan negara
        if (country != null && !country.isEmpty()) {
            shows = shows.stream()
                    .filter(show -> show.getCountry().equalsIgnoreCase(country))
                    .collect(Collectors.toList());
        }

        // Filter tambahan berdasarkan tahun
        if (year != null && !year.isEmpty()) {
            shows = shows.stream()
                    .filter(show -> String.valueOf(show.getDate().getYear()).equals(year))
                    .collect(Collectors.toList());
        }

        // Ambil data dinamis untuk dropdown filter
        Set<String> availableCountries = shows.stream()
                .map(Show::getCountry)
                .collect(Collectors.toSet());

        Set<Integer> availableYears = shows.stream()
                .map(show -> show.getDate().getYear())
                .collect(Collectors.toSet());

        Set<String> availableArtists = shows.stream()
                .map(show -> show.getArtist().getName())
                .collect(Collectors.toSet());

        // Tambahkan data ke model untuk digunakan di template
        model.addAttribute("shows", shows);
        model.addAttribute("query", query);
        model.addAttribute("countries", availableCountries);
        model.addAttribute("years", availableYears);
        model.addAttribute("artists", availableArtists);

        return "search_results";
    }
}
