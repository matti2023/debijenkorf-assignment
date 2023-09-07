package com.debijenkorf.debijenkorfassignment.service.helper;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.debijenkorf.debijenkorfassignment.config.AWSConfiguration;
import com.debijenkorf.debijenkorfassignment.config.PredefinedImageTypeConfiguration;
import com.debijenkorf.debijenkorfassignment.domain.PredefinedImageType;
import com.debijenkorf.debijenkorfassignment.exception.PredefinedImageTypeNotFoundException;
import com.debijenkorf.debijenkorfassignment.exception.WriteToS3Exception;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ClientService {

    private final PredefinedImageTypeConfiguration predefinedTypes;
    private final AWSConfiguration awsConfigurationService;
    private final AmazonS3 s3Client;

    public boolean isImagePresentInS3(String type, String reference) {
        return s3Client.doesObjectExist(awsConfigurationService.getEndpoint(), createS3Key(type, reference));
    }

    public boolean isImagePresentInS3(String s3Key) {
        return s3Client.doesObjectExist(awsConfigurationService.getEndpoint(), s3Key);
    }

    public byte[] retrieveImageFromS3(String type, String reference) {
        S3Object s3Object = s3Client.getObject(awsConfigurationService.getEndpoint(), createS3Key(type, reference));
        try (InputStream inputStream = s3Object.getObjectContent()) {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error reading image from S3", e);
        }
    }

    public byte[] retrieveImageFromS3(String s3Key) {
        S3Object s3Object = s3Client.getObject(awsConfigurationService.getEndpoint(), s3Key);
        try (InputStream inputStream = s3Object.getObjectContent()) {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error reading image from S3", e);
        }
    }

    public void storeImageInS3(String s3Key, byte[] imageData) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageData.length);
        InputStream inputStream = new ByteArrayInputStream(imageData);
        try {
            s3Client.putObject(awsConfigurationService.getEndpoint(), s3Key, inputStream, metadata);
        } catch (SdkClientException e) {
            log.warn("Error writing image to S3", e);
            retryWriting(s3Key, metadata, inputStream);
        }
    }

    private void retryWriting(String s3Key, ObjectMetadata metadata, InputStream inputStream) {
        try {
            Thread.sleep(200);
            s3Client.putObject(awsConfigurationService.getEndpoint(), s3Key, inputStream, metadata);
        } catch (InterruptedException | SdkClientException retryException) {
            throw new WriteToS3Exception("Error writing image to S3 after retry", retryException);
        }
    }

    public void deleteAllOptimisedImagesFromS3(String reference) {
        for (PredefinedImageType imageType : predefinedTypes.getTypes()) {
            String s3Key = createS3Key(imageType.getName(), reference);
            deleteImageFromS3(s3Key);
        }
    }

    public void deleteImageFromS3(String s3Key) {
        if (isImagePresentInS3(s3Key)) {
            s3Client.deleteObject(awsConfigurationService.getEndpoint(), s3Key);
        }
    }

    public String createS3Key(String type, String reference) {
        String extension = reference.substring(reference.lastIndexOf("."));
        String sanitizedReference = escapeSlashes(reference).substring(0, reference.lastIndexOf("."));
        return type + "/" + buildSubDirectories(sanitizedReference)
            + sanitizedReference + extension;
    }

    private String escapeSlashes(String input) {
        Pattern slashPattern = Pattern.compile("/");
        Matcher matcher = slashPattern.matcher(input);
        return matcher.replaceAll("_");
    }

    private String buildSubDirectories(String sanitizedReference) {
        int length = sanitizedReference.length();
        if (length > 8) {
            return sanitizedReference.substring(0, 4) + "/" + sanitizedReference.substring(4, 8) + "/";
        } else if (length > 4) {
            return sanitizedReference.substring(0, 4) + "/";
        } else {
            return sanitizedReference;
        }
    }

    public void validatePredefinedTypes(String type) {
        if (!predefinedTypes.getTypes().stream().map(PredefinedImageType::getName).toList().contains(type)) {
            throw new PredefinedImageTypeNotFoundException(
                "The requested predefined image type does not exist: " + type);
        }
    }
}
