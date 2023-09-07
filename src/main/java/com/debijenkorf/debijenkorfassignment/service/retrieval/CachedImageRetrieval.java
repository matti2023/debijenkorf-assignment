package com.debijenkorf.debijenkorfassignment.service.retrieval;

import com.debijenkorf.debijenkorfassignment.service.ImageRetrievalChain;
import com.debijenkorf.debijenkorfassignment.service.helper.S3ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CachedImageRetrieval implements ImageRetrievalChain {

    private final S3ClientService s3ClientService;
    private ImageRetrievalChain next;

    @Autowired
    public CachedImageRetrieval(S3ClientService s3ClientService) {
        this.s3ClientService = s3ClientService;
    }

    @Override
    public byte[] handleRequest(String type, String reference) {
        s3ClientService.validatePredefinedTypes(type);
        if (s3ClientService.isImagePresentInS3(type, reference)) {
            return s3ClientService.retrieveImageFromS3(type, reference);
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
