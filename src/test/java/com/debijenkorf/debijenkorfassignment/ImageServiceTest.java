package com.debijenkorf.debijenkorfassignment;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.debijenkorf.debijenkorfassignment.service.ImageRetrievalChain;
import com.debijenkorf.debijenkorfassignment.service.ImageService;
import com.debijenkorf.debijenkorfassignment.service.helper.ImageOptimiserService;
import com.debijenkorf.debijenkorfassignment.service.helper.S3ClientService;
import com.debijenkorf.debijenkorfassignment.service.helper.SourceImageDownloaderService;
import com.debijenkorf.debijenkorfassignment.service.retrieval.CachedImageRetrieval;
import com.debijenkorf.debijenkorfassignment.service.retrieval.OriginalImageRetrieval;
import com.debijenkorf.debijenkorfassignment.service.retrieval.SourceImageRetrieval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ImageServiceTest {

    @Mock
    private S3ClientService s3ClientService;
    @Mock
    private SourceImageDownloaderService sourceImageDownloaderService;
    @Mock
    private ImageOptimiserService imageOptimiserService;
    private ImageService imageService;

    @BeforeEach
    void setUp() {

        ImageRetrievalChain chain = new CachedImageRetrieval(s3ClientService);
        ImageRetrievalChain originalImageRetrieval = new OriginalImageRetrieval(s3ClientService,
            imageOptimiserService);
        ImageRetrievalChain sourceImageRetrieval = new SourceImageRetrieval(s3ClientService,
            imageOptimiserService, sourceImageDownloaderService);

        chain.setNext(originalImageRetrieval);
        originalImageRetrieval.setNext(sourceImageRetrieval);

        imageService = new ImageService(chain, null);
    }

    @Test
    void testGetOptimizedImage_CachedImageRetrieval() {
        String type = "thumbnail";
        String reference = "abcde.jpg";
        byte[] expectedImage = new byte[]{1, 2, 3};
        when(s3ClientService.isImagePresentInS3(type, reference)).thenReturn(true);
        when(s3ClientService.retrieveImageFromS3(type, reference)).thenReturn(expectedImage);

        byte[] result = imageService.getOptimizedImage(type, reference);

        verify(s3ClientService, times(1)).isImagePresentInS3(type, reference);
        verify(s3ClientService, times(1)).retrieveImageFromS3(type, reference);
        Assertions.assertArrayEquals(expectedImage, result);
    }

    @Test
    void testGetOptimizedImage_OriginalImageRetrieval() {
        String type = "thumbnail";
        String reference = "abcde.jpg";

        byte[] expectedOriginalImage = new byte[]{4, 5, 6};
        byte[] expectedOptimisedImage = new byte[]{4};

        when(s3ClientService.isImagePresentInS3(type, reference)).thenReturn(
            false);
        String s3KeyOriginal = "original/abcd/abcde.jpg";
        String s3KeyOptimised = "thumbnail/abcd/abcde.jpg";

        when(s3ClientService.createS3Key("original", reference)).thenReturn(s3KeyOriginal);
        when(s3ClientService.createS3Key(type, reference)).thenReturn(s3KeyOptimised);

        when(s3ClientService.isImagePresentInS3(s3KeyOriginal)).thenReturn(true);
        when(s3ClientService.retrieveImageFromS3(s3KeyOriginal)).thenReturn(expectedOriginalImage);
        when(imageOptimiserService.optimiseImage(expectedOriginalImage, type)).thenReturn(expectedOptimisedImage);

        byte[] result = imageService.getOptimizedImage(type, reference);

        verify(s3ClientService, times(1)).isImagePresentInS3(type, reference);
        verify(s3ClientService, times(1)).isImagePresentInS3(s3KeyOriginal);
        verify(s3ClientService, times(1)).retrieveImageFromS3(s3KeyOriginal);
        verify(s3ClientService, times(1)).storeImageInS3(s3KeyOptimised, result);

        verify(imageOptimiserService, times(1)).optimiseImage(expectedOriginalImage, type);

        Assertions.assertArrayEquals(expectedOptimisedImage, result);
    }

    @Test
    void testGetOptimizedImage_SourceImageRetrieval() {
        String type = "thumbnail";
        String reference = "abcde.jpg";

        byte[] expectedOriginalImage = new byte[]{4, 5, 6};
        byte[] expectedOptimisedImage = new byte[]{4};

        when(s3ClientService.isImagePresentInS3(type, reference)).thenReturn(
            false);
        String s3KeyOriginal = "original/abcd/abcde.jpg";
        String s3KeyOptimised = "thumbnail/abcd/abcde.jpg";

        when(s3ClientService.createS3Key("original", reference)).thenReturn(s3KeyOriginal);
        when(s3ClientService.createS3Key(type, reference)).thenReturn(s3KeyOptimised);

        when(s3ClientService.isImagePresentInS3(s3KeyOriginal)).thenReturn(false);
        when(sourceImageDownloaderService.downloadOriginalImage(reference)).thenReturn(expectedOriginalImage);
        when(imageOptimiserService.optimiseImage(expectedOriginalImage, type)).thenReturn(expectedOptimisedImage);

        byte[] result = imageService.getOptimizedImage(type, reference);

        verify(s3ClientService, times(1)).isImagePresentInS3(type, reference);
        verify(s3ClientService, times(1)).isImagePresentInS3(s3KeyOriginal);
        verify(s3ClientService, times(1)).storeImageInS3(s3KeyOriginal, expectedOriginalImage);
        verify(s3ClientService, times(1)).storeImageInS3(s3KeyOptimised, result);

        verify(imageOptimiserService, times(1)).optimiseImage(expectedOriginalImage, type);
        verify(sourceImageDownloaderService, times(1)).downloadOriginalImage(reference);

        Assertions.assertArrayEquals(expectedOptimisedImage, result);
    }

    @Test
    void testFlushImages() {
        //todo: write some tests for flushing
    }
}
