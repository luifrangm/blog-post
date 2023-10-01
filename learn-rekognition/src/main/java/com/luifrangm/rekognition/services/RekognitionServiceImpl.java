package com.luifrangm.rekognition.services;

import com.luifrangm.rekognition.utils.AwsUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

@Slf4j
public class RekognitionServiceImpl implements RekognitionService {


    private static final String BUCKET_S3_NAME = "nombre-bucket-S3";
    private final RekognitionClient rekognitionClient =
        AwsUtils.buildRekognitionClient();
    @Override
    public float compareFaces(String imgSource, String imgTarget) throws Exception {

        /* TODO
            Como alternativa, tambien puede leer imagenes desde un bucket S3
            y usarlas para la comparacion

        S3Object s3ObjectSource = S3Object.builder()
            .bucket(BUCKET_S3_NAME)
            .name("Queen_Elizabeth_II_01.jpg)
            .build();

        S3Object s3ObjectTarget = S3Object.builder()
            .bucket(BUCKET_S3_NAME)
            .name("Queen_Elizabeth_II_02.jpg)
            .build();

        Image sourceImage = Image.builder()
            .s3Object(s3ObjectSource).build();

        Image targetImage = Image.builder()
            .s3Object(s3ObjectTarget).build();
        */

        Image sourceImage = getImageFromFile("Queen_Elizabeth_II_01.jpg");
        Image targetImage = getImageFromFile("Queen_Elizabeth_II_02.jpg");

        if(noDetectFace(sourceImage) || noDetectFace(targetImage)) {
            throw new Exception("No se detecto rostro en una o mas imagenes");
        }

        CompareFacesRequest request =
            CompareFacesRequest.builder()
                .sourceImage(sourceImage)
                .targetImage(targetImage)
                .build();
        return
            Try.of(()->
                rekognitionClient.compareFaces(request)
                    .faceMatches().get(0).similarity())
                .onFailure(throwable ->
                    log.error("Error al comparar las imagenes {}", throwable.getMessage()))
                .getOrNull();
    }

    @Override
    public boolean noDetectFace(Image image) {
        final DetectFacesRequest request =
            DetectFacesRequest.builder()
                .image(image)
                .build();

        return
            rekognitionClient.detectFaces(request)
                .faceDetails().isEmpty();
    }

    private Image getImageFromFile(final String pathToFile) throws Exception {
        return
            Try.of(()->
                Image.builder()
                    .bytes(
                        SdkBytes.fromByteArray(
                            readFile(pathToFile))))
                .getOrElseThrow(throwable ->
                    new Exception("Imagen nula o formato no valido"))
                .build();
    }

    private byte[] readFile(final String pathToFile) {
        return
            Try.of(()->
                RekognitionServiceImpl.class
                    .getClassLoader().getResourceAsStream(pathToFile)
                    .readAllBytes())
                .onFailure(throwable ->
                    log.error("Error al leer el archivo {}",throwable.getMessage()))
                .getOrNull();
    }
}
