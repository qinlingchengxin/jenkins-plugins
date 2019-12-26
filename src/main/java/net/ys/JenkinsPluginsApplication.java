package net.ys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JenkinsPluginsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JenkinsPluginsApplication.class, args);
    }

}
