package com.debijenkorf.debijenkorfassignment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Data
public class PredefinedImageType {

    private String name;
    private int height;
    private int width;
    private int quality;
    private ScaleType scaleType;
    private String fillColor;
    private ImageType imageType;
}
