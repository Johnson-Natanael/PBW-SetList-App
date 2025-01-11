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
            artist.setDescription("Abel Makkonen Tesfaye (bahasa Amhara: አቤል መኮንን ተስፋዬ?) (lahir 16 Februari 1990), lebih dikenal dengan nama panggung The Weeknd, adalah seorang penyanyi, penulis lagu dan produser rekaman dari Kanada.[1]\r\n" + //
                                "\r\n" + //
                                "Pada akhir 2010, Tesfaye secara anonim mengunggah beberapa lagu ke YouTube dengan nama \"The Weeknd\". Ia merilis tiga mixtape sembilan lagu sepanjang 2011: House of Balloons, Thursday dan Echoes of Silence, yang meraih sambutan meriah.[2] Pada 2012, ia merilis sebuah album kompilasi Trilogy, tiga puluh lagu yang terdiri dari mixtape yang dimaster ulang dan tiga lagu tambahan. Album tersebut dirilis di bawah label Republic Records dan labelnya sendiri XO.\r\n" + //
                                "\r\n" + //
                                "Pada 2013, ia merilis album studio debutnya Kiss Land, yang didukung oleh singel-singel \"Kiss Land\" dan \"Live For\". Album keduanya, Beauty Behind the Madness, yang menjadi album nomor satu pertamanya di Billboard 200 AS, meliputi singel nomor tiga \"Earned It\" dan memproduksi singel-singel nomor satu \"The Hills\" dan \"Can't Feel My Face\". Lagu-lagu tersebut masuk tiga besar di tangga lagu Hot R&B Songs Billboard, menjadikannya artis pertama dalam sejarah yang mencapainya.[3] The Weeknd memenangkan dua Grammy Award dan sembilan Juno Award.[4] Pada September 2016, perilisan album ketiga Starboy diumumkan bersama dengan perilisan singel lagu utama Starboy, yang kemudian meraih peringkat satu pada Billboard Hot 100.");
            artist.setImageFilename("the_weeknd.jpg");
            return artistRepository.save(artist);
        });

        Artist artist2 = artistRepository.findByName("Adele").orElseGet(() -> {
            Artist artist = new Artist();
            artist.setName("Adele");
            artist.setDescription("Adele Laurie Blue Adkins (/əˈdɛl/;[5] born 5 May 1988), known mononymously as Adele, is an English singer-songwriter. Regarded as a British icon, she is known for her mezzo-soprano vocals and sentimental songwriting. Her accolades include 16 Grammy Awards, 12 Brit Awards (including three for British Album of the Year), an Academy Award, a Primetime Emmy Award, and a Golden Globe Award.\r\n" + //
                                "\r\n" + //
                                "After graduating in arts from the BRIT School in 2006,[6] Adele signed a record deal with XL Recordings. Her debut album, 19, was released in 2008 and included the UK top-five singles \"Chasing Pavements\" and \"Make You Feel My Love\". 19 was named in the top 20 best-selling debut albums ever in the UK.[7][8] She was honoured with the Grammy Award for Best New Artist. Adele released her second studio album, 21, in 2011. It became the world's best-selling album of the 21st century. 21 holds the record for the top-performing album in US chart history, topping the Billboard 200 for 24 weeks, with the singles \"Rolling in the Deep\", \"Someone like You\", and \"Set Fire to the Rain\" heading charts worldwide, becoming her signature songs. The album received a record-tying six Grammy Awards, including Album of the Year. In 2012, Adele released \"Skyfall\", a soundtrack single for the James Bond film Skyfall, which won her the Academy Award for Best Original Song.");
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
            setlist1.setSongs(List.of("Blinding Lights", "Save Your Tears", "Starboy", "The Hills", "Can't Feel My Face", "Die for You", "Wicked Games"));  // Example songs
            setlist1.setProofFilename("1736590452368_theweekndcons.jpeg");
            setlistRepository.save(setlist1);
        }

        // Cek apakah setlist untuk show2 sudah ada
        Setlist existingSetlist2 = setlistRepository.findByShowId(show2.getId());
        if (existingSetlist2 == null) {
            // Jika tidak ada, buat setlist baru
            Setlist setlist2 = new Setlist();
            setlist2.setShow(show2);
            setlist2.setSongs(List.of("Hello", "Someone Like You", "Set Fire to the Rain", "Rolling in the Deep", "Easy On Me", "All I Ask", "When We Were Young"));  // Example songs
            setlist2.setProofFilename("1736591411165_adele_featured.jpg");
            setlistRepository.save(setlist2);
        }
    }
}
