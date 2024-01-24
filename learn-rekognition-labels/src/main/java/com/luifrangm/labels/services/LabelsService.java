package com.luifrangm.labels.services;

import com.luifrangm.labels.models.LabelsResponse;
import software.amazon.awssdk.services.rekognition.model.CustomLabel;

import java.util.Optional;

public interface LabelsService {
    Optional<LabelsResponse> getLabels(String imgSource);
}
