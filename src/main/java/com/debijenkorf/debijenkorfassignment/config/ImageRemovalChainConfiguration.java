package com.debijenkorf.debijenkorfassignment.config;

import com.debijenkorf.debijenkorfassignment.service.removal.OriginalImageRemoval;
import com.debijenkorf.debijenkorfassignment.service.removal.SpecificTypeImageRemoval;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ImageRemovalChainConfiguration {

    private final OriginalImageRemoval originalImageRemoval;

    private final SpecificTypeImageRemoval specificTypeImageRemoval;

    @PostConstruct
    public void configureChains() {
        originalImageRemoval.setNext(specificTypeImageRemoval);
    }
}
