package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(App.class, args);
        var service = ctx.getBean(MailService.class);

        service.sendEmailFromTemplate("gabriel210814@gmail.com", "mail/hello", "subject 1");
        service.sendEmailFromTemplate("gabriel210814@gmail.com", "mail/bye", "subject 2");
        service.sendEmail("gabriel210814@gmail.com", "subject 3", "Hello world content", false, false);

    }

}
