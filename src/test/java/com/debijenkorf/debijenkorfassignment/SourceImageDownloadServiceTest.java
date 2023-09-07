package com.debijenkorf.debijenkorfassignment;

import com.debijenkorf.debijenkorfassignment.service.helper.SourceImageDownloaderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SourceImageDownloadServiceTest {

    @Autowired
    private SourceImageDownloaderService sourceImageDownloadService;

    @Test
    void testDownloadOriginalImage_ImageExist() {
        byte[] bytes = sourceImageDownloadService.downloadOriginalImage("body-bg.png");
        Assertions.assertNotNull(bytes);
    }
}
