package com.luifrangm.rekognition.services;

import software.amazon.awssdk.services.rekognition.model.Image;

public interface RekognitionService {

    float compareFaces(String source, String target) throws Exception;
    boolean noDetectFace(Image image);

}
