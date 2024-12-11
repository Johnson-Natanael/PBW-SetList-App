package com.example.ProjectSetlist.config;

import com.example.ProjectSetlist.model.*;
import com.example.ProjectSetlist.repository.*;
import com.example.ProjectSetlist.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private SetlistRepository setlistRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Initialize Roles
        Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_USER");
            return roleRepository.save(role);
        });

        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        });

        // Create Admin User if not exists
        Optional<User> existingAdminOpt = userRepository.findByUsername("admin");
        if (existingAdminOpt.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("adminpassword")); // Use a strong password
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
        }

        // Create Regular User if not exists
        Optional<User> existingUserOpt = userRepository.findByUsername("user");
        if (existingUserOpt.isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("userpassword"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        }

        // Add Artists
        Artist artist1 = artistRepository.findByName("The Weeknd").orElseGet(() -> {
            Artist artist = new Artist();
            artist.setName("The Weeknd");
            artist.setDescription("The Weeknd is a Canadian singer, songwriter, and record producer.");
            artist.setImageFilename("the_weeknd.jpg");
            return artistRepository.save(artist);
        });

        Artist artist2 = artistRepository.findByName("Adele").orElseGet(() -> {
            Artist artist = new Artist();
            artist.setName("Adele");
            artist.setDescription("Adele Laurie Blue Adkins is an English singer-songwriter.");
            artist.setImageFilename("adele.jpg");
            return artistRepository.save(artist);
        });

        // Add Shows for Artists
        Show show1 = showRepository.findByArtistAndVenue(artist1, "Madison Square Garden").orElseGet(() -> {
            Show show = new Show();
            show.setArtist(artist1);
            show.setVenue("Madison Square Garden");
            show.setCity("New York");
            show.setCountry("USA");
            show.setDate(LocalDate.of(2024, 12, 15)); // Example date
            return showRepository.save(show);
        });

        Show show2 = showRepository.findByArtistAndVenue(artist2, "O2 Arena").orElseGet(() -> {
            Show show = new Show();
            show.setArtist(artist2);
            show.setVenue("O2 Arena");
            show.setCity("London");
            show.setCountry("UK");
            show.setDate(LocalDate.of(2024, 12, 20)); // Example date
            return showRepository.save(show);
        });

        // Add Setlists for Shows
        // Cek apakah setlist untuk show1 sudah ada
        Setlist existingSetlist1 = setlistRepository.findByShowId(show1.getId());
        if (existingSetlist1 == null) {
            // Jika tidak ada, buat setlist baru
            Setlist setlist1 = new Setlist();
            setlist1.setShow(show1);
            setlist1.setSongs(List.of("Blinding Lights", "Save Your Tears", "Starboy"));  // Example songs
            setlist1.setProofFilename("proof1.jpg");
            setlistRepository.save(setlist1);
        }

        // Cek apakah setlist untuk show2 sudah ada
        Setlist existingSetlist2 = setlistRepository.findByShowId(show2.getId());
        if (existingSetlist2 == null) {
            // Jika tidak ada, buat setlist baru
            Setlist setlist2 = new Setlist();
            setlist2.setShow(show2);
            setlist2.setSongs(List.of("Hello", "Someone Like You", "Set Fire to the Rain"));  // Example songs
            setlist2.setProofFilename("proof2.jpg");
            setlistRepository.save(setlist2);
        }
    }
}
