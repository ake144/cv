package com.firstone.cv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.firstone.cv.repository")
public class CvApplication {

    public static void main(String[] args) {
        SpringApplication.run(CvApplication.class, args);
    }

}
