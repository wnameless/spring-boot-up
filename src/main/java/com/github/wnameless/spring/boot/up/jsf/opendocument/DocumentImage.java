package com.github.wnameless.spring.boot.up.jsf.opendocument;

import java.io.InputStream;

public class DocumentImage {

  private final InputStream imageStream;
  private final String fileName;
  private final Double widthCm;
  private final Double heightCm;

  public DocumentImage(InputStream imageStream, String fileName) {
    this(imageStream, fileName, null, null);
  }

  public DocumentImage(InputStream imageStream, String fileName, Double widthCm, Double heightCm) {
    if (imageStream == null) {
      throw new IllegalArgumentException("Image stream cannot be null");
    }
    if (fileName == null || fileName.trim().isEmpty()) {
      throw new IllegalArgumentException("File name cannot be null or empty");
    }
    this.imageStream = imageStream;
    this.fileName = fileName;
    this.widthCm = widthCm;
    this.heightCm = heightCm;
  }

  public InputStream getImageStream() {
    return imageStream;
  }

  public String getFileName() {
    return fileName;
  }

  public Double getWidthCm() {
    return widthCm;
  }

  public Double getHeightCm() {
    return heightCm;
  }

  public boolean hasCustomDimensions() {
    return widthCm != null && heightCm != null;
  }
}