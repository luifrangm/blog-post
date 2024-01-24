package com.luifrangm.labels.services;

import com.luifrangm.labels.models.LabelsGeometry;
import software.amazon.awssdk.services.rekognition.model.Geometry;

import java.io.File;

public interface DrawService {
    public File draw(File file, LabelsGeometry geometry);
}
