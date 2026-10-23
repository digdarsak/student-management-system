package com.sms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // First start only: creates the admin account and 10,500 students (takes a few seconds)
    @Bean
    CommandLineRunner seed(AdminRepository admins, StudentRepository students, PasswordEncoder encoder) {
        return args -> {
            if (admins.count() == 0) {
                admins.save(new AdminUser("admin", encoder.encode("admin123")));
            }
            if (students.count() == 0) {
                System.out.println("Creating 10,500 sample students...");
                students.saveAll(SampleData.generate(10_500));
                System.out.println("Done. Open http://localhost:8080");
            }
        };
    }
}
