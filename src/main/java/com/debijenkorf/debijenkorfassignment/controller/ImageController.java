package com.debijenkorf.debijenkorfassignment.controller;

import com.debijenkorf.debijenkorfassignment.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping({"/show/{type}/", "/show/{type}/{seoName}"})
    public ResponseEntity<Resource> showImage(
        @PathVariable String type,
        @PathVariable(required = false, value = "seoName") String seoName,
        @RequestParam("reference") String reference) {
        byte[] optimizedImage = imageService.getOptimizedImage(type, reference);
        return new ResponseEntity<>(new ByteArrayResource(optimizedImage), HttpStatus.OK);
    }

    @DeleteMapping("/flush/{type}")
    public void flushImage(
        @PathVariable String type,
        @RequestParam("reference") String reference) {
        imageService.flushImages(type, reference);
    }
}
