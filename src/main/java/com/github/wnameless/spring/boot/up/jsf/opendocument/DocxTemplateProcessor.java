package com.github.wnameless.spring.boot.up.jsf.opendocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

public class DocxTemplateProcessor {

  private static final String PLACEHOLDER_REGEX = "\\{\\{(.+?)\\}\\}";

  /**
   * Processes a .docx template from an InputStream, replacing placeholders with provided text or
   * images, and returns the resulting document as a byte array.
   *
   * @param templateInputStream InputStream of the .docx template file.
   * @param textReplacements Map of placeholder keywords to replacement text.
   * @param imageReplacements Map of placeholder keywords to list of image resource paths.
   * @return Byte array of the processed .docx file.
   * @throws Exception If an error occurs during processing.
   */
  public byte[] processTemplate(InputStream templateInputStream,
      Map<String, String> textReplacements, Map<String, List<String>> imageReplacements)
      throws Exception {
    return processTemplate(templateInputStream, textReplacements, imageReplacements, null);
  }

  /**
   * Processes a .docx template from an InputStream, replacing placeholders with provided text,
   * static images, or dynamic images, and returns the resulting document as a byte array.
   *
   * @param templateInputStream InputStream of the .docx template file.
   * @param textReplacements Map of placeholder keywords to replacement text.
   * @param imageReplacements Map of placeholder keywords to list of image resource paths.
   * @param documentImageReplacements Map of placeholder keywords to list of DocumentImage objects
   *        with custom dimensions.
   * @return Byte array of the processed .docx file.
   * @throws Exception If an error occurs during processing.
   */
  public byte[] processTemplate(InputStream templateInputStream,
      Map<String, String> textReplacements, Map<String, List<String>> imageReplacements,
      Map<String, List<DocumentImage>> documentImageReplacements) throws Exception {

    XWPFDocument document = new XWPFDocument(templateInputStream);

    // Process paragraphs
    for (XWPFParagraph paragraph : document.getParagraphs()) {
      replaceInParagraph(paragraph, textReplacements, imageReplacements, documentImageReplacements);
    }

    // Process headers
    for (XWPFHeader header : document.getHeaderList()) {
      for (XWPFParagraph paragraph : header.getParagraphs()) {
        replaceInParagraph(paragraph, textReplacements, imageReplacements,
            documentImageReplacements);
      }
    }

    // Process footers
    for (XWPFFooter footer : document.getFooterList()) {
      for (XWPFParagraph paragraph : footer.getParagraphs()) {
        replaceInParagraph(paragraph, textReplacements, imageReplacements,
            documentImageReplacements);
      }
    }

    // Process tables
    for (XWPFTable table : document.getTables()) {
      for (XWPFTableRow row : table.getRows()) {
        for (XWPFTableCell cell : row.getTableCells()) {
          for (XWPFParagraph paragraph : cell.getParagraphs()) {
            replaceInParagraph(paragraph, textReplacements, imageReplacements,
                documentImageReplacements);
          }
        }
      }
    }

    // Write the processed document to a ByteArrayOutputStream
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    document.write(outputStream);

    // Close resources
    document.close();
    templateInputStream.close();
    outputStream.close();

    // Return the byte array of the processed document
    return outputStream.toByteArray();
  }

  private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> textReplacements,
      Map<String, List<String>> imageReplacements,
      Map<String, List<DocumentImage>> documentImageReplacements) throws Exception {
    List<XWPFRun> runs = paragraph.getRuns();
    if (runs == null || runs.size() == 0) {
      return;
    }

    StringBuilder paragraphText = new StringBuilder();
    for (XWPFRun run : runs) {
      String text = run.getText(0);
      if (text != null) {
        paragraphText.append(text);
      }
    }

    Pattern pattern = Pattern.compile(PLACEHOLDER_REGEX);
    Matcher matcher = pattern.matcher(paragraphText.toString());

    if (matcher.find()) {
      int startIndex = matcher.start();
      int endIndex = matcher.end();

      // Now, find which runs contain the placeholder
      int runStartIndex = -1;
      int runEndIndex = -1;
      int currentCharIndex = 0;

      for (int i = 0; i < runs.size(); i++) {
        XWPFRun run = runs.get(i);
        String runText = run.getText(0);
        if (runText != null) {
          int runLength = runText.length();
          if (runStartIndex == -1 && currentCharIndex + runLength > startIndex) {
            runStartIndex = i;
          }
          if (runStartIndex != -1 && currentCharIndex + runLength >= endIndex) {
            runEndIndex = i;
            break;
          }
          currentCharIndex += runLength;
        }
      }

      if (runStartIndex != -1 && runEndIndex != -1) {
        // Preserve style
        var rpr = preserveRunStyle(runs.get(runStartIndex));

        String placeholder = matcher.group(0);
        String key = matcher.group(1).trim();

        if (textReplacements != null && textReplacements.containsKey(key)) {
          // Replace placeholder with text while preserving formatting
          String replacementText = textReplacements.get(key);
          if (replacementText == null) replacementText = "";

          // Handle the runs containing the placeholder
          StringBuilder runText = new StringBuilder();
          for (int i = runStartIndex; i <= runEndIndex; i++) {
            XWPFRun run = runs.get(i);
            runText.append(run.getText(0));
          }

          String updatedRunText = runText.toString().replace(placeholder, replacementText);

          // Remove the runs containing the placeholder
          for (int i = runEndIndex; i >= runStartIndex; i--) {
            paragraph.removeRun(i);
          }

          // Insert new runs with the updated text, handling newlines
          String[] lines = updatedRunText.split("\n", -1);
          for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            XWPFRun newRun = paragraph.insertNewRun(runStartIndex + i);
            if (rpr != null) newRun.getCTR().setRPr(rpr);
            newRun.setText(line, 0);

            if (i < lines.length - 1) {
              // Add a line break after each line except the last
              newRun.addBreak();
            }
          }

          // Recursively handle any other placeholders in the paragraph
          replaceInParagraph(paragraph, textReplacements, imageReplacements,
              documentImageReplacements);

        } else if (imageReplacements != null && imageReplacements.containsKey(key)) {
          // Remove runs containing the placeholder
          for (int i = runEndIndex; i >= runStartIndex; i--) {
            paragraph.removeRun(i);
          }

          List<String> imagePaths = imageReplacements.get(key);

          if (imagePaths != null && !imagePaths.isEmpty()) {
            // Insert images side by side
            int insertPosition = runStartIndex;
            for (String imagePath : imagePaths) {
              InputStream imgInputStream = getImageInputStream(imagePath);

              if (imgInputStream == null) {
                throw new Exception("Image not found: " + imagePath);
              }

              String imgFileName = getImageFileName(imagePath);
              int imgFormat = getImageFormat(imgFileName);

              XWPFRun imageRun = paragraph.insertNewRun(insertPosition++);
              imageRun.addPicture(imgInputStream, imgFormat, imgFileName, Units.toEMU(100),
                  Units.toEMU(100)); // Adjust image size as needed
              imgInputStream.close();

              // Optionally, add a space between images
              XWPFRun spaceRun = paragraph.insertNewRun(insertPosition++);
              spaceRun.setText(" ");
            }
            // Remove the extra space run at the end
            if (insertPosition > runStartIndex) {
              paragraph.removeRun(insertPosition - 1);
            }

            // Recursively handle any other placeholders in the paragraph
            replaceInParagraph(paragraph, textReplacements, imageReplacements,
                documentImageReplacements);
          }
        } else if (documentImageReplacements != null
            && documentImageReplacements.containsKey(key)) {
          // Remove runs containing the placeholder
          for (int i = runEndIndex; i >= runStartIndex; i--) {
            paragraph.removeRun(i);
          }

          List<DocumentImage> documentImages = documentImageReplacements.get(key);

          if (documentImages != null && !documentImages.isEmpty()) {
            // Insert images side by side
            int insertPosition = runStartIndex;
            for (DocumentImage documentImage : documentImages) {
              // Insert image with custom dimensions
              InputStream imgInputStream = documentImage.getImageStream();
              String imgFileName = documentImage.getFileName();
              int imgFormat = getImageFormat(imgFileName);

              XWPFRun imageRun = paragraph.insertNewRun(insertPosition++);

              // Calculate dimensions
              int width, height;
              if (documentImage.hasCustomDimensions()) {
                // Convert cm to EMU (1 cm = 360000 EMU)
                width = (int) (documentImage.getWidthCm() * 360000);
                height = (int) (documentImage.getHeightCm() * 360000);
              } else {
                // Use default size
                width = Units.toEMU(100);
                height = Units.toEMU(100);
              }

              imageRun.addPicture(imgInputStream, imgFormat, imgFileName, width, height);

              // Optionally, add a space between images
              XWPFRun spaceRun = paragraph.insertNewRun(insertPosition++);
              spaceRun.setText(" ");
            }
            // Remove the extra space run at the end
            if (insertPosition > runStartIndex) {
              paragraph.removeRun(insertPosition - 1);
            }

            // Recursively handle any other placeholders in the paragraph
            replaceInParagraph(paragraph, textReplacements, imageReplacements,
                documentImageReplacements);
          }
        }
      }
    }
  }

  private InputStream getImageInputStream(String imagePath) throws Exception {
    // Try to load image from classpath
    InputStream imgInputStream = getClass().getClassLoader().getResourceAsStream(imagePath);
    if (imgInputStream != null) {
      return imgInputStream;
    }

    // If not found in classpath, try to load from file system
    File imgFile = new File(imagePath);
    if (imgFile.exists()) {
      return new FileInputStream(imgFile);
    }

    // Image not found
    return null;
  }

  private String getImageFileName(String imagePath) {
    // Extract the file name from the path
    return new File(imagePath).getName();
  }

  private int getImageFormat(String imgFileName) throws InvalidFormatException {
    int format;
    String lowerCaseName = imgFileName.toLowerCase();
    if (lowerCaseName.endsWith(".emf"))
      format = XWPFDocument.PICTURE_TYPE_EMF;
    else if (lowerCaseName.endsWith(".wmf"))
      format = XWPFDocument.PICTURE_TYPE_WMF;
    else if (lowerCaseName.endsWith(".pict"))
      format = XWPFDocument.PICTURE_TYPE_PICT;
    else if (lowerCaseName.endsWith(".jpeg") || lowerCaseName.endsWith(".jpg"))
      format = XWPFDocument.PICTURE_TYPE_JPEG;
    else if (lowerCaseName.endsWith(".png"))
      format = XWPFDocument.PICTURE_TYPE_PNG;
    else if (lowerCaseName.endsWith(".dib"))
      format = XWPFDocument.PICTURE_TYPE_DIB;
    else if (lowerCaseName.endsWith(".gif"))
      format = XWPFDocument.PICTURE_TYPE_GIF;
    else if (lowerCaseName.endsWith(".tiff"))
      format = XWPFDocument.PICTURE_TYPE_TIFF;
    else if (lowerCaseName.endsWith(".eps"))
      format = XWPFDocument.PICTURE_TYPE_EPS;
    else if (lowerCaseName.endsWith(".bmp"))
      format = XWPFDocument.PICTURE_TYPE_BMP;
    else if (lowerCaseName.endsWith(".wpg"))
      format = XWPFDocument.PICTURE_TYPE_WPG;
    else
      throw new InvalidFormatException("Unsupported picture format: " + imgFileName);
    return format;
  }

  private CTRPr preserveRunStyle(XWPFRun sourceRun) {
    if (sourceRun.getCTR() != null && sourceRun.getCTR().getRPr() != null) {
      return (CTRPr) sourceRun.getCTR().getRPr().copy();
    }
    return null;
  }

}
