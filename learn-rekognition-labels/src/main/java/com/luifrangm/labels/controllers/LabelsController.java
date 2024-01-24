package com.luifrangm.labels.controllers;

import com.luifrangm.labels.models.LabelsResponse;
import com.luifrangm.labels.services.LabelsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.rekognition.model.CustomLabel;

@RestController
@RequestMapping("label")
@RequiredArgsConstructor
public class LabelsController {

    private final LabelsService labelsService;

    @GetMapping(
        value = "/{imageName}",
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LabelsResponse> detectLabel(@PathVariable("imageName") final String imageName) {

        final LabelsResponse response =
            labelsService.getLabels(imageName).orElse(null);
        System.out.println("Resultado: " + response);
        return
            new ResponseEntity<>(response, HttpStatus.OK);
    }

}
