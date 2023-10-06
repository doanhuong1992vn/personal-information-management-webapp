package com.user_service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class UserServiceApplication {
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUrl;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @EventListener({ApplicationReadyEvent.class})
    public void applicationReadyEvent() {
        System.out.println("Application started ... launching browser now");
        openBrowser("http://localhost:8080" + swaggerUrl);
    }

    private void openBrowser(String swaggerUrl) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                processBuilder.command("cmd", "/c", "start", swaggerUrl);
            } else if (os.contains("mac")) {
                processBuilder.command("open", swaggerUrl);
            } else if (os.contains("nix") || os.contains("nux")) {
                processBuilder.command("xdg-open", swaggerUrl);
            }
            processBuilder.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Desktop is not supported. Please open the browser manually.");
        }
    }

}
