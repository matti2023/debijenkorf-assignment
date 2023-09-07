package com.debijenkorf.debijenkorfassignment.service.helper;

import java.util.Arrays;
import org.springframework.stereotype.Service;

@Service
public class ImageOptimiserService {

    //todo: optimisation algorithms should be implemented here
    public byte[] optimiseImage(byte[] originalImageData, String type) {
        return Arrays.copyOf(originalImageData, 1);
    }
}
