package com.debijenkorf.debijenkorfassignment.service;

import com.debijenkorf.debijenkorfassignment.exception.ImageNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    private final ImageRetrievalChain imageRetrievalChain;
    private final ImageRemovalChain imageRemovalChain;

    @Autowired
    public ImageService(@Qualifier("cachedImageRetrieval") ImageRetrievalChain imageRetrievalChain,
        @Qualifier("originalImageRemoval") ImageRemovalChain imageRemovalChain) {
        this.imageRetrievalChain = imageRetrievalChain;
        this.imageRemovalChain = imageRemovalChain;
    }

    public byte[] getOptimizedImage(String type, String reference) throws ImageNotFoundException {
        return imageRetrievalChain.handleRequest(type, reference);
    }

    public void flushImages(String type, String reference) {
        imageRemovalChain.handleRequest(type, reference);
    }
}
