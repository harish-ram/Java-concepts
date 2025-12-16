package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"main","web","data","services","models","utilities","tools","gui"})
public class SpringBootApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }

    @Bean
    public ApplicationRunner runner() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                System.out.println("Spring Boot context initialized.");
                System.out.println("Next: implement controllers and wire repositories (see TODO list).");
            }
        };
    }
}
