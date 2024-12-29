package com.example.ProjectSetlist.controller;

import com.example.ProjectSetlist.model.Role;
import com.example.ProjectSetlist.model.User;
import com.example.ProjectSetlist.repository.RoleRepository;
import com.example.ProjectSetlist.repository.UserRepository;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //Show Admin Dashboard
    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    //List All Users
    @GetMapping("/users")
    public String listUsers(@RequestParam(value = "message", required = false) String message, Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
    
        if (message != null) {
            model.addAttribute("message", message);
        }
    
        return "admin/users";
    }
    

    //Show Create User Form
    @GetMapping("/users/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        return "admin/create_user";
    }

    //Handle Create User
    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("user") User user,
                             BindingResult result,
                             @RequestParam("roleIds") List<Long> roleIds,
                             Model model) {
        if (result.hasErrors()) {
            List<Role> roles = roleRepository.findAll();
            model.addAttribute("roles", roles);
            return "admin/create_user";
        }
      
        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign roles
        Set<Role> roles = Set.copyOf(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        user.setRegisteredAt(LocalDateTime.now());

        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/admin/users";
        }

        System.out.println("galloooo");
        User user = userOpt.get();
        model.addAttribute("user", user);
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        return "admin/edit_user";
    }

    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Long id,
                        @Valid @ModelAttribute("user") User user,
                        BindingResult result,
                        @RequestParam("roleIds") List<Long> roleIds,
                        Model model) {


        // Log data roleIds yang dikirim
        System.out.println("Role IDs received: " + roleIds);

        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            return "redirect:/admin/users";  // Jika user tidak ditemukan
        }

        User existingUser = existingUserOpt.get();

        // Update role
       Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        existingUser.setRoles(roles);


        userRepository.save(existingUser);  // Simpan perubahan ke database

        return "redirect:/admin/users";  
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/reports")
    public String showReports(Model model) {
        // Example Report: Number of Users by Role
        List<Role> roles = roleRepository.findAll();
        List<RoleReport> roleReports = roles.stream().map(role -> {
            long count = userRepository.countByRoles_Name(role.getName());
            return new RoleReport(role.getName(), count);
        }).collect(Collectors.toList());

        model.addAttribute("roleReports", roleReports);
        return "admin/reports";
    }

    @GetMapping("/reports/pdf")
    public ResponseEntity<byte[]> generatePdfReport() throws Exception {
        // Ambil data dari database (jumlah user berdasarkan tanggal registrasi)
        List<User> users = userRepository.findAll();
        Map<String, Long> userRegistrations = users.stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRegisteredAt().toLocalDate().toString(),
                        Collectors.counting()
                ));

        // Buat grafik menggunakan JFreeChart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        userRegistrations.forEach((date, count) -> dataset.addValue(count, "Users", date));
        JFreeChart chart = ChartFactory.createLineChart(
                "User Registrations Over Time",
                "Date",
                "Number of Users",
                dataset
        );

        // Konversi grafik menjadi BufferedImage
        BufferedImage chartImage = chart.createBufferedImage(600, 400);

        // Buat PDF menggunakan iText
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        // Tambahkan judul ke PDF
        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        document.add(new Paragraph("User Registration Report", titleFont));
        document.add(new Paragraph(" ")); // Spasi kosong

        // Tambahkan grafik ke PDF
        Image pdfChart = Image.getInstance(chartImage, null);
        pdfChart.setAlignment(Image.ALIGN_CENTER);
        document.add(pdfChart);

        document.close();

        // Kembalikan PDF sebagai respons
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "UserRegistrationReport.pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .body(baos.toByteArray());
    }

    public static class RoleReport {
        private String roleName;
        private long userCount;

        public RoleReport(String roleName, long userCount) {
            this.roleName = roleName;
            this.userCount = userCount;
        }

        // Getters and Setters
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }

        public long getUserCount() { return userCount; }
        public void setUserCount(long userCount) { this.userCount = userCount; }
    }
}