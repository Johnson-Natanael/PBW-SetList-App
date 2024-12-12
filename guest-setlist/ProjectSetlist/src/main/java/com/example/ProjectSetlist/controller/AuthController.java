package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Role;
import com.example.ProjectSetlist.model.User;
import com.example.ProjectSetlist.repository.RoleRepository;
import com.example.ProjectSetlist.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") @Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("usernameError", "Username is already taken");
            return "register";
        }

        // Handle Optional<Role> correctly
        Optional<Role> memberRoleOpt = roleRepository.findByName("ROLE_MEMBER");
        Role memberRole = memberRoleOpt.orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_MEMBER");
            roleRepository.save(role);
            System.out.println("Created role: ROLE_MEMBER");
            return role;
        });

        user.setRoles(Collections.singleton(memberRole));
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        System.out.println("New user registered with username: " + user.getUsername());
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}