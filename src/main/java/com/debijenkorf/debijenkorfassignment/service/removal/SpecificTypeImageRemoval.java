package com.debijenkorf.debijenkorfassignment.service.removal;

import com.debijenkorf.debijenkorfassignment.service.ImageRemovalChain;
import com.debijenkorf.debijenkorfassignment.service.helper.S3ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SpecificTypeImageRemoval implements ImageRemovalChain {

    private final S3ClientService s3ClientService;

    @Autowired
    public SpecificTypeImageRemoval(S3ClientService s3ClientService) {
        this.s3ClientService = s3ClientService;
    }

    @Override
    public void handleRequest(String type, String reference) {
        String s3Key = s3ClientService.createS3Key(type, reference);
        s3ClientService.deleteImageFromS3(s3Key);
    }

    @Override
    public void setNext(ImageRemovalChain next) {
        log.info("last step of image removal");
    }
}
