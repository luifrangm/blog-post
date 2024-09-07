package com.luifrangm.rekognition.utils;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

public class AwsUtils {

    private AwsUtils() {}

    private static final String ACCES_KEY = "XXXXXXXXXX";
    private static final String SECRET_KEY = "XXXXXXXXXXXXXXXXXXXXXXX";

    private static AwsCredentials getAwsCredentials() {

        return AwsBasicCredentials.create(ACCES_KEY, SECRET_KEY);
    }

    public static AwsCredentialsProvider getAwsCredentialsProvider() {
        return
            StaticCredentialsProvider.create(getAwsCredentials());
    }

    public static RekognitionClient buildRekognitionClient() {
        return
            RekognitionClient.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(getAwsCredentialsProvider())
                .build();
    }

}
