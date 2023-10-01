package com.luifrangm.rekognition;

import com.luifrangm.rekognition.services.RekognitionService;
import com.luifrangm.rekognition.services.RekognitionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {

        SpringApplication.run(Main.class,args);

        RekognitionService service =
            new RekognitionServiceImpl();

        System.out.println("realizando la comparacion ...");

        System.out.println("Similitud: " +
            service.compareFaces("Elizabeth ii.jpg","Queen_Elizabeth_II_in_March_2015.jpg"));

    }
}