package com.debijenkorf.debijenkorfassignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.debijenkorf.debijenkorfassignment.config.PredefinedImageTypeConfiguration;
import com.debijenkorf.debijenkorfassignment.domain.PredefinedImageType;
import com.debijenkorf.debijenkorfassignment.exception.PredefinedImageTypeNotFoundException;
import com.debijenkorf.debijenkorfassignment.service.helper.S3ClientService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class S3ClientServiceTest {

    @InjectMocks
    private S3ClientService s3ClientService;

    @Mock
    private PredefinedImageTypeConfiguration predefinedTypes;

    @ParameterizedTest
    @MethodSource("s3KeyProvider")
    void testCreateS3Key(String type, String reference, String expectedS3Key) {
        String s3Key = s3ClientService.createS3Key(type, reference);
        assertEquals(expectedS3Key, s3Key);
    }

    private static Stream<Arguments> s3KeyProvider() {
        return Stream.of(
            Arguments.of("thumbnail", "abcdefghij.jpg", "thumbnail/abcd/efgh/abcdefghij.jpg"),
            Arguments.of("thumbnail", "abcde.jpg", "thumbnail/abcd/abcde.jpg"),
            Arguments.of("thumbnail", "/somedir/anotherdir/abcdef.jpg",
                "thumbnail/_som/edir/_somedir_anotherdir_abcdef.jpg")
        );
    }

    @Test
    void testValidatePredefinedTypes_ValidType() {
        String validImageType = "original";

        List<PredefinedImageType> predefinedImageTypes = Arrays.asList(
            PredefinedImageType.builder().name("original").build(),
            PredefinedImageType.builder().name("thumbnail").build()
        );
        when(predefinedTypes.getTypes()).thenReturn(predefinedImageTypes);
        s3ClientService.validatePredefinedTypes(validImageType);
    }

    @Test
    void testValidatePredefinedTypes_InvalidType() {
        String invalidImageType = "invalid";
        List<PredefinedImageType> predefinedImageTypes = Arrays.asList(
            PredefinedImageType.builder().name("original").build(),
            PredefinedImageType.builder().name("thumbnail").build()
        );
        when(predefinedTypes.getTypes()).thenReturn(predefinedImageTypes);

        assertThrows(PredefinedImageTypeNotFoundException.class,
            () -> s3ClientService.validatePredefinedTypes(invalidImageType));
    }
}
