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
                // Online, set ADMIN_PASSWORD on the host so the default password is never used
                String password = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123");
                admins.save(new AdminUser("admin", encoder.encode(password)));
            }
            if (students.count() == 0) {
                int count = Integer.parseInt(System.getenv().getOrDefault("SEED_COUNT", "10500"));
                System.out.println("Creating " + count + " sample students...");
                students.saveAll(SampleData.generate(count));
                System.out.println("Done. Open http://localhost:8080");
            }
        };
    }
}
