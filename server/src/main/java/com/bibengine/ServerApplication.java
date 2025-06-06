package com.bibengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.bibengine.user.UserService;
import com.bibengine.user.UserRepository;
import com.bibengine.user.User;

@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    /** Tworzy domyślnego administratora, jeśli brak użytkowników */
    @Bean
    CommandLineRunner init(UserRepository repo, UserService service) {
        return args -> {
            if (repo.count() == 0) {
                User admin = service.register("admin", "admin@example.com", "admin123");
                admin.setRole("ROLE_ADMIN");
                repo.save(admin);
            }
        };
    }
}
