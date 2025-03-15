package com.sid.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class TrackerManagerService {

    public static void main(String[] args) {
        SpringApplication.run(TrackerManagerService.class, args);
    }

    // Print all the Bean names
    /*
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TrackerManagerService.class, args);
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Arrays.stream(beanDefinitionNames).sorted().forEach(System.out::println);
    }
    */
}