package com.github.wnameless.spring.boot.up.jsf.opendocument;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class DocxPlaceholderExtractor {

  private static final String PLACEHOLDER_REGEX = "\\{\\{(.*?)\\}\\}";

  /**
   * Extracts all unique placeholders enclosed in double curly braces from a .docx input stream.
   *
   * @param docxInputStream InputStream of the .docx file.
   * @return A Set containing all unique placeholder words.
   * @throws Exception If an error occurs during file processing.
   */
  public Set<String> extractPlaceholders(InputStream docxInputStream) throws Exception {
    Set<String> placeholders = new LinkedHashSet<>();

    try (XWPFDocument document = new XWPFDocument(docxInputStream)) {

      // Extract from paragraphs
      for (XWPFParagraph paragraph : document.getParagraphs()) {
        extractFromParagraph(paragraph, placeholders);
      }

      // Extract from headers
      for (XWPFHeader header : document.getHeaderList()) {
        for (XWPFParagraph paragraph : header.getParagraphs()) {
          extractFromParagraph(paragraph, placeholders);
        }
      }

      // Extract from footers
      for (XWPFFooter footer : document.getFooterList()) {
        for (XWPFParagraph paragraph : footer.getParagraphs()) {
          extractFromParagraph(paragraph, placeholders);
        }
      }

      // Extract from tables
      for (XWPFTable table : document.getTables()) {
        extractFromTable(table, placeholders);
      }
    }

    return placeholders;
  }

  private void extractFromParagraph(XWPFParagraph paragraph, Set<String> placeholders) {
    String paragraphText = getFullParagraphText(paragraph);

    Pattern pattern = Pattern.compile(PLACEHOLDER_REGEX);
    Matcher matcher = pattern.matcher(paragraphText);

    while (matcher.find()) {
      String placeholder = matcher.group(1).trim();
      placeholders.add(placeholder);
    }
  }

  private void extractFromTable(XWPFTable table, Set<String> placeholders) {
    for (XWPFTableRow row : table.getRows()) {
      for (XWPFTableCell cell : row.getTableCells()) {
        // Extract from paragraphs in cells
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
          extractFromParagraph(paragraph, placeholders);
        }
        // Recursively extract from nested tables
        for (XWPFTable nestedTable : cell.getTables()) {
          extractFromTable(nestedTable, placeholders);
        }
      }
    }
  }

  private String getFullParagraphText(XWPFParagraph paragraph) {
    StringBuilder paragraphText = new StringBuilder();
    for (XWPFRun run : paragraph.getRuns()) {
      String text = run.getText(0);
      if (text != null) {
        paragraphText.append(text);
      }
    }
    return paragraphText.toString();
  }

}
