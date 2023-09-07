package com.debijenkorf.debijenkorfassignment.service.removal;

import com.debijenkorf.debijenkorfassignment.service.ImageRemovalChain;
import com.debijenkorf.debijenkorfassignment.service.helper.S3ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OriginalImageRemoval implements ImageRemovalChain {

    static final String ORIGINAL = "original";
    private ImageRemovalChain next;
    private final S3ClientService s3ClientService;

    @Autowired
    public OriginalImageRemoval(S3ClientService s3ClientService) {
        this.s3ClientService = s3ClientService;
    }

    @Override
    public void handleRequest(String type, String reference) {

        if (ORIGINAL.equalsIgnoreCase(type)) {
            s3ClientService.deleteAllOptimisedImagesFromS3(reference);
        } else if (next != null) {
            next.handleRequest(type, reference);
        }
    }

    @Override
    public void setNext(ImageRemovalChain next) {
        this.next = next;
    }
}
