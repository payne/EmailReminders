package pl.piomin.services.emailreminders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EmailRemindersApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailRemindersApplication.class, args);
    }
}
