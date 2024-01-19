package com.wrbread.roll.rollingpaper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RollingPaperApplication {

    public static void main(String[] args) {
        SpringApplication.run(RollingPaperApplication.class, args);
    }

}
