package com.luifrangm.labels;

import com.luifrangm.labels.services.LabelsService;
import com.luifrangm.labels.services.LabelsServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import software.amazon.awssdk.services.rekognition.model.S3Object;

@SpringBootApplication
@ComponentScan("com.luifrangm.labels")
public class Main {
    public static void main(String[] args) {


        SpringApplication.run(Main.class,args);

    }
}