package com.debijenkorf.debijenkorfassignment.config;

import com.debijenkorf.debijenkorfassignment.service.retrieval.CachedImageRetrieval;
import com.debijenkorf.debijenkorfassignment.service.retrieval.OriginalImageRetrieval;
import com.debijenkorf.debijenkorfassignment.service.retrieval.SourceImageRetrieval;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ImageRetrievalChainConfiguration {

    private final CachedImageRetrieval cachedImageRetrieval;

    private final OriginalImageRetrieval originalImageRetrieval;

    private final SourceImageRetrieval sourceImageDownloadStrategy;

    @PostConstruct
    public void configureChains() {
        cachedImageRetrieval.setNext(originalImageRetrieval);
        originalImageRetrieval.setNext(sourceImageDownloadStrategy);
    }
}
