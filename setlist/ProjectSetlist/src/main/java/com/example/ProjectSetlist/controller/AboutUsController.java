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
public class AboutUsController {

    @GetMapping("/aboutus")
    public String showAboutUsPage() {
        return "about_us";
    }
}
