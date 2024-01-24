package com.luifrangm.labels.services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.luifrangm.labels.models.LabelsGeometry;
import com.luifrangm.labels.models.LabelsResponse;
import com.luifrangm.labels.utils.AwsUtils;
import com.luifrangm.labels.utils.LabelUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LabelsServiceImpl implements LabelsService {

    private static final String PROJECT_ARN = "arn:aws:rekognition:us-east-2:572067824011:project/zenta-logo-project/version/zenta-logo-project.2024-01-23T22.33.46/1706060027883";
    private static final String BUCKET_S3_NAME = "luifrangm-zenta-images";
    private static final String PATH_IMAGE = "pruebas/";
    private static final String PATH_OUTPUT = "pruebas/output/";
    private static final float MIN_CONFIDENCE = 80F;

    private final RekognitionClient rekognitionClient =
        AwsUtils.buildRekognitionClient();

    private final AmazonS3Client s3 = new AmazonS3Client()
        .withRegion(Regions.US_EAST_2);

     private final DrawService drawService;

    @Override
    public Optional<LabelsResponse> getLabels(String imgSource) {

        S3Object s3ObjectSource = S3Object.builder()
            .bucket(BUCKET_S3_NAME)
            .name(PATH_IMAGE.concat(imgSource))
            .build();

        Image image = Image.builder()
            .s3Object(s3ObjectSource).build();


        File file = s3ObjectToFile(imgSource);


         final DetectCustomLabelsRequest request =
             DetectCustomLabelsRequest.builder()
                 .image(image)
                 .projectVersionArn(PROJECT_ARN)
                 .minConfidence(MIN_CONFIDENCE)
                 .build();

         final LabelsResponse response =
             toLabelResponse(
                 rekognitionClient.detectCustomLabels(request));

        File outputFile = drawService.draw(file,response.getGeometry());

        s3.putObject(BUCKET_S3_NAME,PATH_IMAGE.concat(LabelUtils.renameFileName(imgSource)),outputFile);

         return Optional.of(response);

    }

    private LabelsResponse toLabelResponse( final DetectCustomLabelsResponse response) {

        if(response.customLabels().isEmpty()) {
            throw new RuntimeException("No se encontraron etiquetas Zenta en esta imagen");
        }

        final CustomLabel customLabel = response.customLabels().get(0);

        final LabelsGeometry geometry =
            LabelsGeometry.builder()
                .left(customLabel.geometry().boundingBox().left())
                .top(customLabel.geometry().boundingBox().top())
                .height(customLabel.geometry().boundingBox().height())
                .width(customLabel.geometry().boundingBox().width())
                .build();

        return
            LabelsResponse.builder()
                .labelName(customLabel.name())
                .confidence(customLabel.confidence())
                .geometry(geometry)
                .build();
    }

    private File s3ObjectToFile( final String imgSource) {
        final AmazonS3Client s3 = new AmazonS3Client()
            .withRegion(Regions.US_EAST_2);
        InputStream in = s3.getObject(BUCKET_S3_NAME, PATH_IMAGE.concat(imgSource)).getObjectContent();
        File file = new File(imgSource);

        try {
            FileUtils.copyInputStreamToFile(in, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

}
