package com.github.wnameless.spring.boot.up.jsf.opendocument;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for working with DocumentImage objects
 */
public class DocumentImageUtils {

  private static final Pattern DATA_URL_PATTERN = Pattern.compile(
      "^data:([^;]+);name=([^;]+);base64,(.+)$"
  );

  /**
   * Creates a DocumentImage from a JSON Schema Form base64 data URL.
   * 
   * @param base64DataUrl The data URL in format: "data:image/png;name=abc.png;base64,<base64string>"
   * @return A DocumentImage object with the decoded image data and filename
   * @throws IllegalArgumentException if the data URL format is invalid
   */
  public static DocumentImage fromJsonSchemaFormBase64DataUrl(String base64DataUrl) {
    if (base64DataUrl == null || base64DataUrl.isEmpty()) {
      throw new IllegalArgumentException("Base64 data URL cannot be null or empty");
    }

    Matcher matcher = DATA_URL_PATTERN.matcher(base64DataUrl);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          "Invalid data URL format. Expected: data:<mimeType>;name=<filename>;base64,<data>");
    }

    String mimeType = matcher.group(1);
    String encodedFileName = matcher.group(2);
    String base64Data = matcher.group(3);

    // Validate MIME type
    if (!mimeType.startsWith("image/")) {
      throw new IllegalArgumentException("Invalid MIME type: " + mimeType + ". Expected an image type.");
    }

    // URL decode the filename
    String fileName;
    try {
      fileName = URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to decode filename: " + encodedFileName, e);
    }

    // Validate filename has an extension
    if (!fileName.contains(".")) {
      throw new IllegalArgumentException("Filename must have an extension: " + fileName);
    }

    // Decode base64 data to byte array
    byte[] imageData;
    try {
      imageData = Base64.getDecoder().decode(base64Data);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid base64 data", e);
    }

    // Create InputStream from byte array
    InputStream imageStream = new ByteArrayInputStream(imageData);

    // Return DocumentImage with decoded data
    return new DocumentImage(imageStream, fileName);
  }

  /**
   * Creates a DocumentImage from a JSON Schema Form base64 data URL with custom dimensions.
   * 
   * @param base64DataUrl The data URL in format: "data:image/png;name=abc.png;base64,<base64string>"
   * @param widthCm Width in centimeters
   * @param heightCm Height in centimeters
   * @return A DocumentImage object with the decoded image data, filename, and dimensions
   * @throws IllegalArgumentException if the data URL format is invalid
   */
  public static DocumentImage fromJsonSchemaFormBase64DataUrl(String base64DataUrl, 
      Double widthCm, Double heightCm) {
    if (base64DataUrl == null || base64DataUrl.isEmpty()) {
      throw new IllegalArgumentException("Base64 data URL cannot be null or empty");
    }

    Matcher matcher = DATA_URL_PATTERN.matcher(base64DataUrl);
    if (!matcher.matches()) {
      throw new IllegalArgumentException(
          "Invalid data URL format. Expected: data:<mimeType>;name=<filename>;base64,<data>");
    }

    String mimeType = matcher.group(1);
    String encodedFileName = matcher.group(2);
    String base64Data = matcher.group(3);

    // Validate MIME type
    if (!mimeType.startsWith("image/")) {
      throw new IllegalArgumentException("Invalid MIME type: " + mimeType + ". Expected an image type.");
    }

    // URL decode the filename
    String fileName;
    try {
      fileName = URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to decode filename: " + encodedFileName, e);
    }

    // Validate filename has an extension
    if (!fileName.contains(".")) {
      throw new IllegalArgumentException("Filename must have an extension: " + fileName);
    }

    // Decode base64 data to byte array
    byte[] imageData;
    try {
      imageData = Base64.getDecoder().decode(base64Data);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid base64 data", e);
    }

    // Create InputStream from byte array
    InputStream imageStream = new ByteArrayInputStream(imageData);

    // Return DocumentImage with decoded data and dimensions
    return new DocumentImage(imageStream, fileName, widthCm, heightCm);
  }

  /**
   * Validates if a string is a valid base64 data URL format.
   * 
   * @param base64DataUrl The string to validate
   * @return true if the format is valid, false otherwise
   */
  public static boolean isValidBase64DataUrl(String base64DataUrl) {
    if (base64DataUrl == null || base64DataUrl.isEmpty()) {
      return false;
    }
    
    Matcher matcher = DATA_URL_PATTERN.matcher(base64DataUrl);
    if (!matcher.matches()) {
      return false;
    }
    
    String mimeType = matcher.group(1);
    String base64Data = matcher.group(3);
    
    // Check if it's an image MIME type
    if (!mimeType.startsWith("image/")) {
      return false;
    }
    
    // Validate base64 data
    try {
      Base64.getDecoder().decode(base64Data);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Extracts the filename from a base64 data URL.
   * 
   * @param base64DataUrl The data URL
   * @return The decoded filename, or null if invalid format
   */
  public static String extractFileName(String base64DataUrl) {
    if (base64DataUrl == null || base64DataUrl.isEmpty()) {
      return null;
    }

    Matcher matcher = DATA_URL_PATTERN.matcher(base64DataUrl);
    if (!matcher.matches()) {
      return null;
    }

    String encodedFileName = matcher.group(2);
    try {
      return URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Extracts the MIME type from a base64 data URL.
   * 
   * @param base64DataUrl The data URL
   * @return The MIME type, or null if invalid format
   */
  public static String extractMimeType(String base64DataUrl) {
    if (base64DataUrl == null || base64DataUrl.isEmpty()) {
      return null;
    }

    Matcher matcher = DATA_URL_PATTERN.matcher(base64DataUrl);
    if (!matcher.matches()) {
      return null;
    }

    return matcher.group(1);
  }
}