package com.debijenkorf.debijenkorfassignment.service.retrieval;

import com.debijenkorf.debijenkorfassignment.service.ImageRetrievalChain;
import com.debijenkorf.debijenkorfassignment.service.helper.ImageOptimiserService;
import com.debijenkorf.debijenkorfassignment.service.helper.S3ClientService;
import com.debijenkorf.debijenkorfassignment.service.helper.SourceImageDownloaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SourceImageRetrieval implements ImageRetrievalChain {

    private final S3ClientService s3ClientService;
    private final ImageOptimiserService imageOptimiserService;
    private final SourceImageDownloaderService sourceImageDownloaderService;
    private ImageRetrievalChain next;

    @Autowired
    public SourceImageRetrieval(S3ClientService s3ClientService, ImageOptimiserService imageOptimiserService,
        SourceImageDownloaderService sourceImageDownloaderService) {
        this.s3ClientService = s3ClientService;
        this.imageOptimiserService = imageOptimiserService;
        this.sourceImageDownloaderService = sourceImageDownloaderService;
    }

    @Override
    public byte[] handleRequest(String type, String reference) {
        String originalS3Key = s3ClientService.createS3Key("original", reference);
        String optimisedS3Key = s3ClientService.createS3Key(type, reference);

        byte[] originalImageData = sourceImageDownloaderService.downloadOriginalImage(reference);

        byte[] optimizedImageData = imageOptimiserService.optimiseImage(originalImageData, type);

        s3ClientService.storeImageInS3(originalS3Key, originalImageData);
        s3ClientService.storeImageInS3(optimisedS3Key, optimizedImageData);
        return optimizedImageData;
    }

    @Override
    public void setNext(ImageRetrievalChain next) {
        this.next = next;
    }
}
