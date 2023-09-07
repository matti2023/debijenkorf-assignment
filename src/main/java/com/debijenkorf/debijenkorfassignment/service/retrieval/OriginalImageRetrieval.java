package com.debijenkorf.debijenkorfassignment.service.retrieval;

import com.debijenkorf.debijenkorfassignment.service.ImageRetrievalChain;
import com.debijenkorf.debijenkorfassignment.service.helper.ImageOptimiserService;
import com.debijenkorf.debijenkorfassignment.service.helper.S3ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OriginalImageRetrieval implements ImageRetrievalChain {

    private ImageRetrievalChain next;
    private final S3ClientService s3ClientService;
    private final ImageOptimiserService imageOptimiserService;

    @Autowired
    public OriginalImageRetrieval(S3ClientService s3ClientService, ImageOptimiserService imageOptimiserService) {
        this.s3ClientService = s3ClientService;
        this.imageOptimiserService = imageOptimiserService;
    }

    @Override
    public byte[] handleRequest(String type, String reference) {
        String originalS3Key = s3ClientService.createS3Key("original", reference);

        if (s3ClientService.isImagePresentInS3(originalS3Key)) {
            byte[] originalImageData = s3ClientService.retrieveImageFromS3(originalS3Key);
            byte[] optimizedImageData = imageOptimiserService.optimiseImage(originalImageData,
                type);
            String optimizedS3Key = s3ClientService.createS3Key(type, reference);
            s3ClientService.storeImageInS3(optimizedS3Key, optimizedImageData);
            return optimizedImageData;
        } else if (next != null) {
            return next.handleRequest(type, reference);
        }
        throw new RuntimeException("unhandled scenario!");
    }

    @Override
    public void setNext(ImageRetrievalChain next) {
        this.next = next;
    }
}
