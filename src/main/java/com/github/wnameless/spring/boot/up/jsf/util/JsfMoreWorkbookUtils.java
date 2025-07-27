package com.github.wnameless.spring.boot.up.jsf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.jsf.model.ConditionalDependency;
import com.github.wnameless.spring.boot.up.jsf.model.FieldOrigin;
import com.github.wnameless.spring.boot.up.jsf.model.FlattenedSchemaResult;
import lombok.experimental.UtilityClass;

/**
 * Enhanced workbook utilities for JSON Schema Forms with improved enum handling. This class
 * provides methods to generate Excel workbooks with hidden columns for enum values, enabling proper
 * conditional logic evaluation while maintaining user-friendly display names.
 */
@UtilityClass
public class JsfMoreWorkbookUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  // Row indices for metadata
  private static final int HEADER_ROW = 0;
  private static final int ORIGIN_ROW = 1;
  private static final int STATUS_ROW = 2;
  private static final int HIDDEN_FLAG_ROW = 3; // New row to mark hidden columns
  private static final int DATA_START_ROW = 4;

  // Hidden column markers
  private static final String HIDDEN_COLUMN_MARKER = "HIDDEN_VALUE";
  private static final String VISIBLE_COLUMN_MARKER = "VISIBLE";

  /**
   * Generate an Excel workbook from JSON Schema with enhanced enum handling. Creates hidden columns
   * for enum actual values alongside visible columns with display names.
   */
  public static byte[] generateWorkbookFromSchema(Map<String, Object> schemaMap,
      Map<String, Object> uiSchemaMap) throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    JsonNode uiSchemaNode = objectMapper.convertValue(uiSchemaMap, JsonNode.class);
    return generateWorkbookFromSchema(schemaNode, uiSchemaNode);
  }

  public static byte[] generateWorkbookFromSchema(JsonNode schema, JsonNode uiSchema)
      throws Exception {
    // Convert to v5 schema first
    schema = RjsfSchemaConverter.toRjsfV5Schema(schema, uiSchema);

    // Flatten the schema and get field origins
    FlattenedSchemaResult flattenResult = JsfSchemaUtils.flattenConditionalSchemaWithOrigins(
        objectMapper.treeToValue(schema, new TypeReference<Map<String, Object>>() {}));
    JsonNode flattenedSchema = objectMapper.valueToTree(flattenResult.getFlattenedSchema());

    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      // Create sheets
      XSSFSheet sheet = workbook.createSheet("Data Entry");
      XSSFSheet enumSheet = workbook.createSheet("Enums");
      XSSFSheet legendSheet = workbook.createSheet("Legend");
      workbook.setSheetHidden(workbook.getSheetIndex(enumSheet), true);

      // Setup helpers
      CreationHelper createHelper = workbook.getCreationHelper();
      Drawing<?> drawing = sheet.createDrawingPatriarch();
      DataValidationHelper helper = sheet.getDataValidationHelper();

      // Create header rows
      Row headerRow = sheet.createRow(HEADER_ROW);
      Row originRow = sheet.createRow(ORIGIN_ROW);
      Row statusRow = sheet.createRow(STATUS_ROW);
      Row hiddenFlagRow = sheet.createRow(HIDDEN_FLAG_ROW);

      // Hide metadata rows
      sheet.getRow(ORIGIN_ROW).setZeroHeight(true);
      sheet.getRow(STATUS_ROW).setZeroHeight(true);
      sheet.getRow(HIDDEN_FLAG_ROW).setZeroHeight(true);

      // Process properties
      JsonNode properties = flattenedSchema.get("properties");
      if (properties == null || !properties.isObject()) {
        throw new IllegalArgumentException("No properties found in flattened schema");
      }

      int colIndex = 0;
      int enumColIndex = 0;
      Map<String, Integer> fieldColumnMap = new HashMap<>();
      Map<String, Integer> hiddenColumnMap = new HashMap<>();

      for (Iterator<String> it = properties.fieldNames(); it.hasNext();) {
        String propName = it.next();
        JsonNode prop = properties.get(propName);

        // Get title
        String title = prop.has("title") ? prop.get("title").asText() : propName;

        // Create visible column
        headerRow.createCell(colIndex).setCellValue(title);
        fieldColumnMap.put(propName, colIndex);

        // Set field origin
        FieldOrigin origin = flattenResult.getFieldOrigins().get(propName);
        originRow.createCell(colIndex).setCellValue(origin != null ? origin.name() : "UNKNOWN");

        // Mark as visible column
        hiddenFlagRow.createCell(colIndex).setCellValue(VISIBLE_COLUMN_MARKER);

        // Handle enums with enumNames - create hidden value column
        if (prop.has("enum") && prop.has("enumNames")) {
          colIndex++;

          // Hidden column for actual values
          headerRow.createCell(colIndex).setCellValue(propName + "_value");
          hiddenColumnMap.put(propName, colIndex);
          hiddenFlagRow.createCell(colIndex).setCellValue(HIDDEN_COLUMN_MARKER);
          sheet.setColumnHidden(colIndex, true);

          // Copy origin to hidden column
          originRow.createCell(colIndex).setCellValue(origin != null ? origin.name() : "UNKNOWN");

          // Set up enum dropdown for visible column
          setupEnumDropdown(workbook, sheet, enumSheet, prop, propName,
              fieldColumnMap.get(propName), enumColIndex, helper);

          // Add formula to populate hidden column based on visible selection
          setupHiddenColumnFormula(sheet, prop, propName, fieldColumnMap.get(propName), colIndex);

          enumColIndex++;
        } else if (prop.has("enum")) {
          // Regular enum without enumNames - use actual values in dropdown
          setupEnumDropdown(workbook, sheet, enumSheet, prop, propName, colIndex, enumColIndex,
              helper);
          enumColIndex++;
        }

        // Set condition status formula
        Cell statusCell = statusRow.createCell(fieldColumnMap.get(propName));
        List<ConditionalDependency> deps = flattenResult.getDependenciesForField(propName);
        if (!deps.isEmpty()) {
          ConditionalDependency dep = deps.get(0);
          Integer condFieldCol =
              getConditionColumn(dep.getConditionField(), fieldColumnMap, hiddenColumnMap);
          if (condFieldCol != null) {
            String formula = dep.toExcelFormula(CellReference.convertNumToColString(condFieldCol),
                DATA_START_ROW + 1);
            statusCell.setCellFormula(formula);
          } else {
            statusCell.setCellValue("INACTIVE");
          }
        } else {
          statusCell.setCellValue("ACTIVE");
        }

        // Add comment with property info
        addPropertyComment(sheet, drawing, createHelper, prop, fieldColumnMap.get(propName));

        colIndex++;
      }

      // Apply conditional formatting
      applyConditionalFormatting(sheet, fieldColumnMap, flattenResult);

      // Create legend
      createLegendSheet(legendSheet);

      // Auto-size columns (only visible ones)
      for (Map.Entry<String, Integer> entry : fieldColumnMap.entrySet()) {
        if (!hiddenColumnMap.containsValue(entry.getValue())) {
          sheet.autoSizeColumn(entry.getValue());
        }
      }

      workbook.write(out);
      return out.toByteArray();
    }
  }

  /**
   * Extract JSON data from workbook, reading actual values from hidden columns when available.
   */
  public static LinkedHashMap<Integer, Map<String, Object>> extractJsonDataFromWorkbook(
      byte[] excelBytes, Map<String, Object> schemaMap, Map<String, Object> uiSchemaMap)
      throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    JsonNode uiSchemaNode = objectMapper.convertValue(uiSchemaMap, JsonNode.class);
    return extractJsonDataFromWorkbook(excelBytes, schemaNode, uiSchemaNode);
  }

  public static LinkedHashMap<Integer, Map<String, Object>> extractJsonDataFromWorkbook(
      byte[] excelBytes, JsonNode schema, JsonNode uiSchema) throws Exception {
    var schemaNode = RjsfSchemaConverter.toRjsfV5Schema(schema, uiSchema);
    return extractJsonDataFromWorkbook(excelBytes, schemaNode);
  }

  public static LinkedHashMap<Integer, Map<String, Object>> extractJsonDataFromWorkbook(
      byte[] excelBytes, JsonNode schema) throws Exception {

    Map<String, String> titleToPropertyMap = new HashMap<>();
    Map<String, String> propertyTypeMap = new HashMap<>();
    Map<String, String> propertyFormatMap = new HashMap<>();
    Map<String, Integer> hiddenColumnMap = new HashMap<>();

    JsonNode properties = schema.get("properties");
    if (properties == null || !properties.isObject()) {
      throw new IllegalArgumentException("Invalid JSON Schema: 'properties' is missing");
    }

    // Build property mappings
    for (Iterator<String> it = properties.fieldNames(); it.hasNext();) {
      String propName = it.next();
      JsonNode prop = properties.get(propName);
      String title = prop.has("title") ? prop.get("title").asText() : propName;
      titleToPropertyMap.put(title, propName);
      if (prop.has("type")) propertyTypeMap.put(propName, prop.get("type").asText());
      if (prop.has("format")) propertyFormatMap.put(propName, prop.get("format").asText());
    }

    LinkedHashMap<Integer, Map<String, Object>> result = new LinkedHashMap<>();

    try (ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);
        XSSFWorkbook workbook = new XSSFWorkbook(bis)) {

      Sheet sheet = workbook.getSheet("Data Entry");
      if (sheet == null) return result;

      Row headerRow = sheet.getRow(HEADER_ROW);
      Row hiddenFlagRow = sheet.getRow(HIDDEN_FLAG_ROW);
      if (headerRow == null) return result;

      // Map column indices to property names and identify hidden columns
      Map<Integer, String> columnToPropertyMap = new HashMap<>();
      for (int i = 0; i < headerRow.getLastCellNum(); i++) {
        Cell cell = headerRow.getCell(i);
        if (cell != null && cell.getCellType() == CellType.STRING) {
          String header = cell.getStringCellValue();

          // Check if this is a hidden value column
          if (hiddenFlagRow != null && hiddenFlagRow.getCell(i) != null
              && HIDDEN_COLUMN_MARKER.equals(hiddenFlagRow.getCell(i).getStringCellValue())) {
            // This is a hidden column, map it to the property name (remove "_value" suffix)
            if (header.endsWith("_value")) {
              String propName = header.substring(0, header.length() - 6);
              hiddenColumnMap.put(propName, i);
            }
          } else {
            // Regular visible column
            String propName = titleToPropertyMap.get(header);
            if (propName != null) {
              columnToPropertyMap.put(i, propName);
            }
          }
        }
      }

      // Process data rows
      for (int rowNum = DATA_START_ROW; rowNum <= sheet.getLastRowNum(); rowNum++) {
        Row row = sheet.getRow(rowNum);
        if (row == null || isRowEmpty(row)) continue;

        Map<String, Object> rowData = new HashMap<>();

        for (Map.Entry<Integer, String> entry : columnToPropertyMap.entrySet()) {
          int colIndex = entry.getKey();
          String propKey = entry.getValue();

          // Check if we should read from hidden column instead
          Integer hiddenCol = hiddenColumnMap.get(propKey);
          Cell cell = row.getCell(hiddenCol != null ? hiddenCol : colIndex);

          if (cell == null || cell.getCellType() == CellType.BLANK) continue;

          String type = propertyTypeMap.get(propKey);
          String format = propertyFormatMap.get(propKey);
          Object value = extractCellValue(cell, type, format);

          if (value != null) {
            rowData.put(propKey, value);
          }
        }

        if (!rowData.isEmpty()) {
          result.put(rowNum - DATA_START_ROW + 1, rowData);
        }
      }
    }

    return result;
  }

  /**
   * Export JSON data to workbook with enhanced enum handling.
   */
  public static byte[] exportJsonDataToWorkbook(List<Map<String, Object>> jsonDataList,
      Map<String, Object> schemaMap, Map<String, Object> uiSchemaMap) throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    JsonNode uiSchemaNode = objectMapper.convertValue(uiSchemaMap, JsonNode.class);
    return exportJsonDataToWorkbook(jsonDataList, schemaNode, uiSchemaNode);
  }

  public static byte[] exportJsonDataToWorkbook(List<Map<String, Object>> jsonDataList,
      JsonNode schema, JsonNode uiSchema) throws Exception {

    // First generate empty workbook with structure
    byte[] emptyWorkbook = generateWorkbookFromSchema(schema, uiSchema);

    try (ByteArrayInputStream bis = new ByteArrayInputStream(emptyWorkbook);
        XSSFWorkbook workbook = new XSSFWorkbook(bis);
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      Sheet sheet = workbook.getSheet("Data Entry");
      Row headerRow = sheet.getRow(HEADER_ROW);
      Row hiddenFlagRow = sheet.getRow(HIDDEN_FLAG_ROW);

      // Build column mappings
      Map<String, Integer> propertyToColumnMap = new HashMap<>();
      Map<String, Integer> propertyToHiddenColumnMap = new HashMap<>();
      Map<String, Map<String, String>> enumValueToNameMap = new HashMap<>();

      JsonNode properties = schema.get("properties");

      for (int i = 0; i < headerRow.getLastCellNum(); i++) {
        Cell headerCell = headerRow.getCell(i);
        Cell hiddenFlagCell = hiddenFlagRow.getCell(i);

        if (headerCell != null && hiddenFlagCell != null) {
          String header = headerCell.getStringCellValue();
          String hiddenFlag = hiddenFlagCell.getStringCellValue();

          if (HIDDEN_COLUMN_MARKER.equals(hiddenFlag) && header.endsWith("_value")) {
            // Hidden value column
            String propName = header.substring(0, header.length() - 6);
            propertyToHiddenColumnMap.put(propName, i);
          } else if (VISIBLE_COLUMN_MARKER.equals(hiddenFlag)) {
            // Visible column - find property name
            for (Iterator<String> it = properties.fieldNames(); it.hasNext();) {
              String propName = it.next();
              JsonNode prop = properties.get(propName);
              String title = prop.has("title") ? prop.get("title").asText() : propName;
              if (title.equals(header)) {
                propertyToColumnMap.put(propName, i);

                // Build enum mapping if needed
                if (prop.has("enum") && prop.has("enumNames")) {
                  Map<String, String> valueToName = new HashMap<>();
                  for (int j = 0; j < prop.get("enum").size(); j++) {
                    valueToName.put(prop.get("enum").get(j).asText(),
                        prop.get("enumNames").get(j).asText());
                  }
                  enumValueToNameMap.put(propName, valueToName);
                }
                break;
              }
            }
          }
        }
      }

      // Export data
      int rowIndex = DATA_START_ROW;
      for (Map<String, Object> jsonData : jsonDataList) {
        Row dataRow = sheet.createRow(rowIndex++);

        for (Map.Entry<String, Object> entry : jsonData.entrySet()) {
          String propName = entry.getKey();
          Object value = entry.getValue();

          Integer colIndex = propertyToColumnMap.get(propName);
          if (colIndex == null) continue;

          // Check if this property has enum with display names
          if (enumValueToNameMap.containsKey(propName) && value != null) {
            // Set display name in visible column
            String displayName = enumValueToNameMap.get(propName).get(value.toString());
            if (displayName != null) {
              dataRow.createCell(colIndex).setCellValue(displayName);
            }

            // Set actual value in hidden column
            Integer hiddenCol = propertyToHiddenColumnMap.get(propName);
            if (hiddenCol != null) {
              setCellValue(dataRow.createCell(hiddenCol), value, properties.get(propName));
            }
          } else {
            // Regular field without enum display names
            setCellValue(dataRow.createCell(colIndex), value, properties.get(propName));
          }
        }
      }

      workbook.write(out);
      return out.toByteArray();
    }
  }

  // Helper methods

  private void setupEnumDropdown(XSSFWorkbook workbook, XSSFSheet sheet, XSSFSheet enumSheet,
      JsonNode prop, String propName, int colIndex, int enumColIndex, DataValidationHelper helper) {

    List<String> enumValues = new ArrayList<>();
    JsonNode source = prop.has("enumNames") ? prop.get("enumNames") : prop.get("enum");

    for (JsonNode v : source) {
      enumValues.add(v.asText());
    }

    // Write enum values to enum sheet
    for (int i = 0; i < enumValues.size(); i++) {
      Row enumRow = enumSheet.getRow(i);
      if (enumRow == null) enumRow = enumSheet.createRow(i);
      enumRow.createCell(enumColIndex).setCellValue(enumValues.get(i));
    }

    // Create named range
    String rangeName = "Enum_" + propName.replaceAll("[^a-zA-Z0-9]", "_");
    XSSFName namedRange = workbook.createName();
    namedRange.setNameName(rangeName);
    String colLetter = CellReference.convertNumToColString(enumColIndex);
    String reference = "Enums!$" + colLetter + "$1:$" + colLetter + "$" + enumValues.size();
    namedRange.setRefersToFormula(reference);

    // Create dropdown validation
    CellRangeAddressList addressList =
        new CellRangeAddressList(DATA_START_ROW, 1000, colIndex, colIndex);
    DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
    DataValidation validation = helper.createValidation(constraint, addressList);
    validation.setShowErrorBox(true);
    sheet.addValidationData(validation);
  }

  private void setupHiddenColumnFormula(Sheet sheet, JsonNode prop, String propName, int visibleCol,
      int hiddenCol) {

    if (!prop.has("enum") || !prop.has("enumNames")) return;

    // Build SWITCH formula to convert display name to actual value
    StringBuilder formula = new StringBuilder("IF(");
    formula.append(CellReference.convertNumToColString(visibleCol)).append(DATA_START_ROW + 1);
    formula.append("=\"\",\"\",SWITCH(");
    formula.append(CellReference.convertNumToColString(visibleCol)).append(DATA_START_ROW + 1);

    JsonNode enumValues = prop.get("enum");
    JsonNode enumNames = prop.get("enumNames");

    for (int i = 0; i < enumValues.size(); i++) {
      formula.append(",\"").append(enumNames.get(i).asText()).append("\",");

      // Handle different types of enum values
      JsonNode enumValue = enumValues.get(i);
      if (enumValue.isTextual()) {
        formula.append("\"").append(enumValue.asText()).append("\"");
      } else {
        formula.append(enumValue.asText());
      }
    }

    formula.append(",\"\"))");

    // Apply formula to all data rows
    for (int rowNum = DATA_START_ROW; rowNum <= 1000; rowNum++) {
      Row row = sheet.getRow(rowNum);
      if (row == null) row = sheet.createRow(rowNum);
      Cell hiddenCell = row.createCell(hiddenCol);
      String rowFormula = formula.toString().replace(String.valueOf(DATA_START_ROW + 1),
          String.valueOf(rowNum + 1));
      hiddenCell.setCellFormula(rowFormula);
    }
  }

  private Integer getConditionColumn(String conditionField, Map<String, Integer> fieldColumnMap,
      Map<String, Integer> hiddenColumnMap) {
    // For condition evaluation, use hidden column if available (actual values)
    Integer hiddenCol = hiddenColumnMap.get(conditionField);
    return hiddenCol != null ? hiddenCol : fieldColumnMap.get(conditionField);
  }

  private void addPropertyComment(Sheet sheet, Drawing<?> drawing, CreationHelper createHelper,
      JsonNode prop, int colIndex) {
    ClientAnchor anchor = createHelper.createClientAnchor();
    anchor.setCol1(colIndex);
    anchor.setCol2(colIndex + 2);
    anchor.setRow1(0);
    anchor.setRow2(2);

    Comment comment = drawing.createCellComment(anchor);
    StringBuilder commentText = new StringBuilder();

    if (prop.has("description")) {
      commentText.append(prop.get("description").asText()).append("\n\n");
    }

    commentText.append("Type: ").append(prop.has("type") ? prop.get("type").asText() : "any");

    if (prop.has("format")) {
      commentText.append("\nFormat: ").append(prop.get("format").asText());
    }

    if (prop.has("enum")) {
      commentText.append("\nAllowed values: ");
      for (JsonNode v : prop.get("enum")) {
        commentText.append("\n  - ").append(v.asText());
      }
    }

    comment.setString(createHelper.createRichTextString(commentText.toString()));
    sheet.getRow(HEADER_ROW).getCell(colIndex).setCellComment(comment);
  }

  private void applyConditionalFormatting(Sheet sheet, Map<String, Integer> fieldColumnMap,
      FlattenedSchemaResult flattenResult) {

    SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

    // Calculate total columns including hidden ones
    int totalColumns = fieldColumnMap.size();
    for (String field : fieldColumnMap.keySet()) {
      // Check if this field has a hidden column (enum with enumNames)
      if (flattenResult.getFlattenedSchema().containsKey("properties")) {
        @SuppressWarnings("unchecked")
        Map<String, Object> properties =
            (Map<String, Object>) flattenResult.getFlattenedSchema().get("properties");
        if (properties.containsKey(field)) {
          @SuppressWarnings("unchecked")
          Map<String, Object> prop = (Map<String, Object>) properties.get(field);
          if (prop.containsKey("enum") && prop.containsKey("enumNames")) {
            totalColumns++; // Add hidden column
          }
        }
      }
    }

    // Apply header formatting (field origins)
    applyHeaderFormatting(sheetCF, totalColumns);

    // Apply data cell formatting (conditional fields)
    applyDataCellFormatting(sheetCF, totalColumns, fieldColumnMap, flattenResult);
  }

  private void applyHeaderFormatting(SheetConditionalFormatting sheetCF, int totalColumns) {
    // Define the range for header row only
    CellRangeAddress[] headerRange =
        new CellRangeAddress[] {new CellRangeAddress(HEADER_ROW, HEADER_ROW, 0, totalColumns - 1)};

    // Header Rule 1: ORIGINAL_REQUIRED → Light blue background
    ConditionalFormattingRule requiredRule = sheetCF.createConditionalFormattingRule(
        "INDIRECT(ADDRESS(" + (ORIGIN_ROW + 1) + ",COLUMN(),4))=\"ORIGINAL_REQUIRED\"");
    PatternFormatting requiredFmt = requiredRule.createPatternFormatting();
    requiredFmt.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.index);
    requiredFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
    sheetCF.addConditionalFormatting(headerRange, requiredRule);

    // Header Rule 2: Hidden columns → Light grey background
    ConditionalFormattingRule hiddenRule =
        sheetCF.createConditionalFormattingRule("INDIRECT(ADDRESS(" + (HIDDEN_FLAG_ROW + 1)
            + ",COLUMN(),4))=\"" + HIDDEN_COLUMN_MARKER + "\"");
    PatternFormatting hiddenFmt = hiddenRule.createPatternFormatting();
    hiddenFmt.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.index);
    hiddenFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
    sheetCF.addConditionalFormatting(headerRange, hiddenRule);
  }

  private void applyDataCellFormatting(SheetConditionalFormatting sheetCF, int totalColumns,
      Map<String, Integer> fieldColumnMap, FlattenedSchemaResult flattenResult) {

    // Build column mapping for condition fields with hidden columns
    Map<String, Integer> conditionColumnMap = new HashMap<>();
    for (String field : fieldColumnMap.keySet()) {
      if (flattenResult.getFlattenedSchema().containsKey("properties")) {
        @SuppressWarnings("unchecked")
        Map<String, Object> properties =
            (Map<String, Object>) flattenResult.getFlattenedSchema().get("properties");
        if (properties.containsKey(field)) {
          @SuppressWarnings("unchecked")
          Map<String, Object> prop = (Map<String, Object>) properties.get(field);
          if (prop.containsKey("enum") && prop.containsKey("enumNames")) {
            // This field has a hidden column - use it for conditions
            Integer visibleCol = fieldColumnMap.get(field);
            conditionColumnMap.put(field, visibleCol + 1); // Hidden column is next to visible
          }
        }
      }
    }

    // Apply conditional formatting for each conditional dependency
    for (String field : fieldColumnMap.keySet()) {
      FieldOrigin origin = flattenResult.getFieldOrigins().get(field);
      List<ConditionalDependency> deps = flattenResult.getDependenciesForField(field);

      if (!deps.isEmpty() && origin != null) {
        ConditionalDependency dep = deps.get(0);
        Integer conditionColIndex = conditionColumnMap.get(dep.getConditionField());
        if (conditionColIndex == null) {
          conditionColIndex = fieldColumnMap.get(dep.getConditionField());
        }

        if (conditionColIndex != null) {
          Integer targetColIndex = fieldColumnMap.get(field);

          if (targetColIndex != null) {
            // Create formula that checks condition in the same row
            String formula = String.format("INDIRECT(ADDRESS(ROW(),%d,4))=\"%s\"",
                conditionColIndex + 1, dep.getConditionValue());

            // Apply different colors based on field origin
            IndexedColors bgColor = getColorForOrigin(origin);
            if (bgColor != null) {
              // Create range for just this column in the data area
              CellRangeAddress[] fieldRange = new CellRangeAddress[] {
                  new CellRangeAddress(DATA_START_ROW, 1000, targetColIndex, targetColIndex)};

              ConditionalFormattingRule fieldRule =
                  sheetCF.createConditionalFormattingRule(formula);
              PatternFormatting fieldFmt = fieldRule.createPatternFormatting();
              fieldFmt.setFillBackgroundColor(bgColor.index);
              fieldFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
              sheetCF.addConditionalFormatting(fieldRange, fieldRule);
            }
          }
        }
      }
    }
  }

  private IndexedColors getColorForOrigin(FieldOrigin origin) {
    switch (origin) {
      case THEN_BRANCH:
        return IndexedColors.LIGHT_TURQUOISE;
      case ELSE_BRANCH:
        return IndexedColors.GREY_25_PERCENT;
      case ALL_OF:
        return IndexedColors.VIOLET;
      case ANY_OF:
      case ONE_OF:
        return IndexedColors.PINK;
      default:
        return null; // No special formatting for other origins
    }
  }

  private void createLegendSheet(Sheet legendSheet) {
    XSSFWorkbook workbook = (XSSFWorkbook) legendSheet.getWorkbook();

    // Create styles for legend with actual colors
    CellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle requiredStyle = workbook.createCellStyle();
    requiredStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index);
    requiredStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle allOfStyle = workbook.createCellStyle();
    allOfStyle.setFillForegroundColor(IndexedColors.VIOLET.index);
    allOfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle thenStyle = workbook.createCellStyle();
    thenStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.index);
    thenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle elseStyle = workbook.createCellStyle();
    elseStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
    elseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle anyOneOfStyle = workbook.createCellStyle();
    anyOneOfStyle.setFillForegroundColor(IndexedColors.PINK.index);
    anyOneOfStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle hiddenStyle = workbook.createCellStyle();
    hiddenStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
    hiddenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    int rowNum = 0;

    // Title
    Row titleRow = legendSheet.createRow(rowNum++);
    Cell titleCell = titleRow.createCell(0);
    titleCell.setCellValue("Enhanced Excel Workbook Legend");
    titleCell.setCellStyle(headerStyle);
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    rowNum++;

    // Header formatting section
    Row headerSectionRow = legendSheet.createRow(rowNum++);
    headerSectionRow.createCell(0).setCellValue("HEADER FORMATTING");
    headerSectionRow.getCell(0).setCellStyle(headerStyle);
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    Row headerDescRow = legendSheet.createRow(rowNum++);
    headerDescRow.createCell(0).setCellValue("Column headers show field origin types:");
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    // Header colors
    Row colorHeader = legendSheet.createRow(rowNum++);
    colorHeader.createCell(0).setCellValue("Color");
    colorHeader.createCell(1).setCellValue("Field Origin");
    colorHeader.createCell(2).setCellValue("Description");

    // Required fields header
    Row requiredRow = legendSheet.createRow(rowNum++);
    Cell requiredColorCell = requiredRow.createCell(0);
    requiredColorCell.setCellStyle(requiredStyle);
    requiredRow.createCell(1).setCellValue("Required Fields");
    requiredRow.createCell(2).setCellValue("Original schema required fields (header only)");

    // Hidden value columns header
    Row hiddenRow = legendSheet.createRow(rowNum++);
    Cell hiddenColorCell = hiddenRow.createCell(0);
    hiddenColorCell.setCellStyle(hiddenStyle);
    hiddenRow.createCell(1).setCellValue("Hidden Value Columns");
    hiddenRow.createCell(2).setCellValue("Hidden columns storing actual enum values (header only)");

    rowNum++;

    // Data cell formatting section
    Row dataSectionRow = legendSheet.createRow(rowNum++);
    dataSectionRow.createCell(0).setCellValue("DATA CELL FORMATTING");
    dataSectionRow.getCell(0).setCellStyle(headerStyle);
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    Row dataDescRow = legendSheet.createRow(rowNum++);
    dataDescRow.createCell(0)
        .setCellValue("Input cells change color based on conditional logic in each row:");
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    // Data cell colors
    Row dataColorHeader = legendSheet.createRow(rowNum++);
    dataColorHeader.createCell(0).setCellValue("Color");
    dataColorHeader.createCell(1).setCellValue("Conditional Field");
    dataColorHeader.createCell(2).setCellValue("When Active");

    // Then branch fields (data cells)
    Row thenRow = legendSheet.createRow(rowNum++);
    Cell thenColorCell = thenRow.createCell(0);
    thenColorCell.setCellStyle(thenStyle);
    thenRow.createCell(1).setCellValue("Then Branch Fields");
    thenRow.createCell(2).setCellValue("When if-condition is true in that row");

    // Else branch fields (data cells)
    Row elseRow = legendSheet.createRow(rowNum++);
    Cell elseColorCell = elseRow.createCell(0);
    elseColorCell.setCellStyle(elseStyle);
    elseRow.createCell(1).setCellValue("Else Branch Fields");
    elseRow.createCell(2).setCellValue("When if-condition is false in that row");

    // AllOf fields (data cells)
    Row allOfRow = legendSheet.createRow(rowNum++);
    Cell allOfColorCell = allOfRow.createCell(0);
    allOfColorCell.setCellStyle(allOfStyle);
    allOfRow.createCell(1).setCellValue("All Of Fields");
    allOfRow.createCell(2).setCellValue("When allOf conditions are met in that row");

    // Any/One Of fields (data cells)
    Row anyOneOfRow = legendSheet.createRow(rowNum++);
    Cell anyOneOfColorCell = anyOneOfRow.createCell(0);
    anyOneOfColorCell.setCellStyle(anyOneOfStyle);
    anyOneOfRow.createCell(1).setCellValue("Any/One Of Fields");
    anyOneOfRow.createCell(2).setCellValue("When anyOf/oneOf conditions are met in that row");

    rowNum++;

    // Instructions
    Row instrHeader = legendSheet.createRow(rowNum++);
    instrHeader.createCell(0).setCellValue("INSTRUCTIONS:");
    instrHeader.getCell(0).setCellStyle(headerStyle);
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    Row instr1 = legendSheet.createRow(rowNum++);
    instr1.createCell(0).setCellValue("• Headers show field origins with static colors");
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    Row instr2 = legendSheet.createRow(rowNum++);
    instr2.createCell(0)
        .setCellValue("• Data cells change color based on conditional logic in each row");
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    Row instr3 = legendSheet.createRow(rowNum++);
    instr3.createCell(0).setCellValue("• Each row evaluates conditions independently");
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    Row instr4 = legendSheet.createRow(rowNum++);
    instr4.createCell(0)
        .setCellValue("• Hidden columns store actual enum values for conditional logic");
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    Row instr5 = legendSheet.createRow(rowNum++);
    instr5.createCell(0)
        .setCellValue("• Hidden rows 2-4 contain metadata for conditional formatting");
    legendSheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

    // Auto-size columns
    legendSheet.autoSizeColumn(0);
    legendSheet.autoSizeColumn(1);
    legendSheet.autoSizeColumn(2);
  }

  private boolean isRowEmpty(Row row) {
    for (int i = 0; i < row.getLastCellNum(); i++) {
      Cell cell = row.getCell(i);
      if (cell != null && cell.getCellType() != CellType.BLANK) {
        return false;
      }
    }
    return true;
  }

  private Object extractCellValue(Cell cell, String type, String format) {
    if (cell == null || cell.getCellType() == CellType.BLANK) return null;

    switch (type) {
      case "string":
        if (cell.getCellType() == CellType.NUMERIC && "date".equals(format)) {
          Date date = cell.getDateCellValue();
          DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
          return df.format(date);
        }
        return cell.toString();

      case "number":
      case "integer":
        if (cell.getCellType() == CellType.NUMERIC) {
          double numValue = cell.getNumericCellValue();
          return "integer".equals(type) ? (int) numValue : numValue;
        }
        try {
          return "integer".equals(type) ? Integer.parseInt(cell.toString())
              : Double.parseDouble(cell.toString());
        } catch (NumberFormatException e) {
          return null;
        }

      case "boolean":
        return Boolean.parseBoolean(cell.toString());

      default:
        return cell.toString();
    }
  }

  private void setCellValue(Cell cell, Object value, JsonNode propSchema) {
    if (value == null) return;

    String type = propSchema.has("type") ? propSchema.get("type").asText() : "string";

    switch (type) {
      case "number":
      case "integer":
        if (value instanceof Number) {
          cell.setCellValue(((Number) value).doubleValue());
        }
        break;

      case "boolean":
        cell.setCellValue(value.toString());
        break;

      default:
        cell.setCellValue(value.toString());
    }
  }
}
