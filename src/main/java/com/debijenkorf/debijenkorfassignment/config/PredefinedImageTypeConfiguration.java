package com.debijenkorf.debijenkorfassignment.config;

import com.debijenkorf.debijenkorfassignment.domain.PredefinedImageType;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("predefined-image")
@Data
public class PredefinedImageTypeConfiguration {

    List<PredefinedImageType> types;
}
