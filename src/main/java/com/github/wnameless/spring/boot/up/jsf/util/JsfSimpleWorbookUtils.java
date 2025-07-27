package com.github.wnameless.spring.boot.up.jsf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.jsf.JsfPOJO;
import com.github.wnameless.spring.boot.up.jsf.model.ConditionalDependency;
import com.github.wnameless.spring.boot.up.jsf.model.FieldOrigin;
import com.github.wnameless.spring.boot.up.jsf.model.FlattenedSchemaResult;
import com.github.wnameless.spring.boot.up.web.WebActionAlertHelper.AlertMessages;
import jakarta.validation.Validator;

public class JsfSimpleWorbookUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static byte[] generateExcelBytesFromSchema(JsonNode schema, JsonNode uiSchema)
      throws Exception {
    schema = RjsfSchemaConverter.toRjsfV5Schema(schema, uiSchema);

    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      XSSFSheet sheet = workbook.createSheet("Data Entry");
      XSSFSheet enumSheet = workbook.createSheet("Enums");
      workbook.setSheetHidden(workbook.getSheetIndex(enumSheet), true);

      XSSFRow headerRow = sheet.createRow(0);
      XSSFRow inputRow = sheet.createRow(1);

      DataValidationHelper helper = sheet.getDataValidationHelper();
      CreationHelper createHelper = workbook.getCreationHelper();
      Drawing<?> drawing = sheet.createDrawingPatriarch();

      CellStyle dateStyle = workbook.createCellStyle();
      dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

      CellStyle dateTimeStyle = workbook.createCellStyle();
      dateTimeStyle
          .setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd\"T\"HH:mm:ss"));

      JsonNode properties = schema.get("properties");
      if (properties == null || !properties.isObject()) {
        throw new IllegalArgumentException(
            "Invalid JSON Schema: 'properties' is missing or not an object.");
      }

      Map<String, String> propertyFormatMap = new HashMap<>();
      int colIndex = 0;
      int enumColIndex = 0;

      for (Iterator<String> it = properties.fieldNames(); it.hasNext();) {
        String propName = it.next();
        JsonNode prop = properties.get(propName);

        String title = prop.has("title") ? prop.get("title").asText() : propName;
        headerRow.createCell(colIndex).setCellValue(title);

        if (prop.has("description")) {
          ClientAnchor anchor = createHelper.createClientAnchor();
          anchor.setCol1(colIndex);
          anchor.setCol2(colIndex + 2);
          anchor.setRow1(0);
          anchor.setRow2(3);

          Comment comment = drawing.createCellComment(anchor);
          comment.setString(createHelper.createRichTextString(prop.get("description").asText()));
          comment.setAuthor("Schema");
          headerRow.getCell(colIndex).setCellComment(comment);
        }

        if (prop.has("format")) {
          propertyFormatMap.put(propName, prop.get("format").asText());
        }

        if (prop.has("enum")) {
          List<String> values = new ArrayList<>();
          JsonNode source = prop.has("enumNames") ? prop.get("enumNames") : prop.get("enum");
          for (JsonNode v : source)
            values.add(v.asText());

          for (int i = 0; i < values.size(); i++) {
            Row row = enumSheet.getRow(i);
            if (row == null) row = enumSheet.createRow(i);
            row.createCell(enumColIndex).setCellValue(values.get(i));
          }

          String rangeName = "Enum_" + propName;
          XSSFName namedRange = workbook.createName();
          namedRange.setNameName(rangeName);
          String colLetter = CellReference.convertNumToColString(enumColIndex);
          String reference = "Enums!$" + colLetter + "$1:$" + colLetter + "$" + values.size();
          namedRange.setRefersToFormula(reference);

          CellRangeAddressList addressList = new CellRangeAddressList(1, 100, colIndex, colIndex);
          DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
          DataValidation validation = helper.createValidation(constraint, addressList);
          validation.setShowErrorBox(true);
          sheet.addValidationData(validation);

          enumColIndex++;
        }

        Cell cell = inputRow.createCell(colIndex);
        String format = propertyFormatMap.get(propName);
        if ("date".equals(format)) {
          cell.setCellStyle(dateStyle);
        } else if ("date-time".equals(format)) {
          cell.setCellStyle(dateTimeStyle);
        }

        colIndex++;
      }

      workbook.write(out);
      return out.toByteArray();
    }
  }

  public static byte[] generateExcelBytesFromSchema(Map<String, Object> schemaMap,
      Map<String, Object> uiSchemaMap) throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    JsonNode uiSchemaNode = objectMapper.convertValue(uiSchemaMap, JsonNode.class);
    return generateExcelBytesFromSchema(schemaNode, uiSchemaNode);
  }

  public static LinkedHashMap<Integer, Map<String, Object>> extractJsonDataFromWorkbook(
      byte[] excelBytes, Map<String, Object> schemaMap, Map<String, Object> uiSchemaMap)
      throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    JsonNode uiSchemaNode = objectMapper.convertValue(uiSchemaMap, JsonNode.class);
    return extractJsonDataFromWorkbook(excelBytes, schemaNode, uiSchemaNode);
  }

  public static LinkedHashMap<Integer, Map<String, Object>> extractJsonDataFromWorkbook(
      byte[] excelBytes, Map<String, Object> schemaMap) throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    return extractJsonDataFromWorkbook(excelBytes, schemaNode);
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
    Map<String, Map<String, String>> enumDisplayToValueMap = new HashMap<>();
    Map<String, String> propertyFormatMap = new HashMap<>();

    JsonNode properties = schema.get("properties");
    if (properties == null || !properties.isObject()) {
      throw new IllegalArgumentException(
          "Invalid JSON Schema: 'properties' is missing or not an object.");
    }

    for (Iterator<String> it = properties.fieldNames(); it.hasNext();) {
      String propName = it.next();
      JsonNode prop = properties.get(propName);

      String title = prop.has("title") ? prop.get("title").asText() : propName;
      titleToPropertyMap.put(title, propName);

      if (prop.has("type")) propertyTypeMap.put(propName, prop.get("type").asText());
      if (prop.has("format")) propertyFormatMap.put(propName, prop.get("format").asText());

      if (prop.has("enum") && prop.has("enumNames")) {
        Map<String, String> reverseMap = new HashMap<>();
        for (int i = 0; i < prop.get("enum").size(); i++) {
          reverseMap.put(prop.get("enumNames").get(i).asText(), prop.get("enum").get(i).asText());
        }
        enumDisplayToValueMap.put(propName, reverseMap);
      }
    }

    LinkedHashMap<Integer, Map<String, Object>> result = new LinkedHashMap<>();

    try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
      Sheet sheet = workbook.getSheet("Data Entry");
      if (sheet == null) throw new IllegalArgumentException("Sheet 'Data Entry' not found");

      Row headerRow = sheet.getRow(0);
      if (headerRow == null) throw new IllegalArgumentException("No header row found");

      int colCount = headerRow.getLastCellNum();
      String[] propKeys = new String[colCount];
      for (int i = 0; i < colCount; i++) {
        Cell cell = headerRow.getCell(i);
        if (cell != null) {
          String title = cell.getStringCellValue();
          propKeys[i] = titleToPropertyMap.get(title);
        }
      }

      for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) continue;

        Map<String, Object> item = new HashMap<>();
        boolean isEmpty = true;

        for (int i = 0; i < colCount; i++) {
          String propKey = propKeys[i];
          if (propKey == null) continue;

          Cell cell = row.getCell(i);
          if (cell == null || cell.getCellType() == CellType.BLANK) continue;

          String type = propertyTypeMap.get(propKey);
          String format = propertyFormatMap.get(propKey);
          Object value = null;

          switch (type) {
            case "string":
              if (("date".equals(format) || "date-time".equals(format))
                  && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                DateFormat df = "date".equals(format) ? new SimpleDateFormat("yyyy-MM-dd")
                    : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                value = df.format(date);
              } else {
                String rawString = cell.toString();
                Map<String, String> reverseEnum = enumDisplayToValueMap.get(propKey);
                value = (reverseEnum != null) ? reverseEnum.getOrDefault(rawString, rawString)
                    : rawString;
              }
              break;
            case "number":
            case "integer":
              value = cell.getNumericCellValue();
              break;
            case "boolean":
              value = cell.getBooleanCellValue();
              break;
            case "array":
              List<String> items = Arrays.stream(cell.getStringCellValue().split(","))
                  .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
              value = items;
              break;
            default:
              value = cell.getStringCellValue();
          }

          item.put(propKey, value);
          isEmpty = false;
        }

        if (!isEmpty) result.put(rowIdx, item);
      }
    }

    return result;
  }

  public static byte[] exportJsonDataToWorkbook(List<Map<String, Object>> dataList,
      Map<String, Object> schemaMap, Map<String, Object> uiSchemaMap) throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    JsonNode uiSchemaNode = objectMapper.convertValue(uiSchemaMap, JsonNode.class);
    return exportJsonDataToWorkbook(dataList, schemaNode, uiSchemaNode);
  }

  public static byte[] exportJsonDataToWorkbook(List<Map<String, Object>> dataList,
      Map<String, Object> schemaMap) throws Exception {
    JsonNode schemaNode = objectMapper.convertValue(schemaMap, JsonNode.class);
    return exportJsonDataToWorkbook(dataList, schemaNode);
  }

  public static byte[] exportJsonDataToWorkbook(List<Map<String, Object>> dataList, JsonNode schema,
      JsonNode uiSchema) throws Exception {
    var schemaNode = RjsfSchemaConverter.toRjsfV5Schema(schema, uiSchema);
    return exportJsonDataToWorkbook(dataList, schemaNode);
  }

  public static byte[] exportJsonDataToWorkbook(List<Map<String, Object>> dataList, JsonNode schema)
      throws Exception {
    try (XSSFWorkbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      XSSFSheet sheet = workbook.createSheet("Data Entry");
      XSSFSheet enumSheet = workbook.createSheet("Enums");
      workbook.setSheetHidden(workbook.getSheetIndex(enumSheet), true);

      CreationHelper createHelper = workbook.getCreationHelper();
      Drawing<?> drawing = sheet.createDrawingPatriarch();

      CellStyle dateStyle = workbook.createCellStyle();
      dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

      CellStyle dateTimeStyle = workbook.createCellStyle();
      dateTimeStyle
          .setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd\"T\"HH:mm:ss"));

      JsonNode properties = schema.get("properties");
      if (properties == null || !properties.isObject()) {
        throw new IllegalArgumentException("Invalid JSON Schema: 'properties' missing or invalid.");
      }

      Map<String, String> propertyTitleMap = new LinkedHashMap<>();
      Map<String, String> propertyTypeMap = new HashMap<>();
      Map<String, String> propertyFormatMap = new HashMap<>();
      Map<String, Map<String, String>> enumValueToNameMap = new HashMap<>();

      int colIndex = 0;
      int enumColIndex = 0;

      XSSFRow headerRow = sheet.createRow(0);

      for (Iterator<String> it = properties.fieldNames(); it.hasNext();) {
        String propName = it.next();
        JsonNode prop = properties.get(propName);

        String title = prop.has("title") ? prop.get("title").asText() : propName;
        propertyTitleMap.put(propName, title);

        if (prop.has("type")) propertyTypeMap.put(propName, prop.get("type").asText());
        if (prop.has("format")) propertyFormatMap.put(propName, prop.get("format").asText());

        headerRow.createCell(colIndex).setCellValue(title);

        // Comment (description)
        if (prop.has("description")) {
          ClientAnchor anchor = createHelper.createClientAnchor();
          anchor.setCol1(colIndex);
          anchor.setCol2(colIndex + 2);
          anchor.setRow1(0);
          anchor.setRow2(3);

          Comment comment = drawing.createCellComment(anchor);
          comment.setString(createHelper.createRichTextString(prop.get("description").asText()));
          comment.setAuthor("Schema");
          headerRow.getCell(colIndex).setCellComment(comment);
        }

        // Dropdown setup (enums)
        if (prop.has("enum")) {
          List<String> enumNames = new ArrayList<>();
          JsonNode source = prop.has("enumNames") ? prop.get("enumNames") : prop.get("enum");

          for (JsonNode name : source)
            enumNames.add(name.asText());

          // Create enum value to name mapping
          if (prop.has("enumNames")) {
            Map<String, String> valueToNameMapping = new HashMap<>();
            for (int i = 0; i < prop.get("enum").size(); i++) {
              valueToNameMapping.put(prop.get("enum").get(i).asText(),
                  prop.get("enumNames").get(i).asText());
            }
            enumValueToNameMap.put(propName, valueToNameMapping);
          }

          for (int i = 0; i < enumNames.size(); i++) {
            Row enumRow = enumSheet.getRow(i);
            if (enumRow == null) enumRow = enumSheet.createRow(i);
            enumRow.createCell(enumColIndex).setCellValue(enumNames.get(i));
          }

          String rangeName = "Enum_" + propName;
          XSSFName namedRange = workbook.createName();
          namedRange.setNameName(rangeName);
          String colLetter = CellReference.convertNumToColString(enumColIndex);
          String reference = "Enums!$" + colLetter + "$1:$" + colLetter + "$" + enumNames.size();
          namedRange.setRefersToFormula(reference);

          DataValidationHelper helper = sheet.getDataValidationHelper();
          CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, colIndex, colIndex);
          DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
          DataValidation validation = helper.createValidation(constraint, addressList);
          validation.setShowErrorBox(true);
          sheet.addValidationData(validation);

          enumColIndex++;
        }

        colIndex++;
      }

      // Write data
      int rowIndex = 1;
      for (Map<String, Object> rowMap : dataList) {
        Row row = sheet.createRow(rowIndex++);
        colIndex = 0;

        for (String prop : propertyTitleMap.keySet()) {
          Object value = rowMap.get(prop);
          if (value == null) {
            colIndex++;
            continue;
          }

          Cell cell = row.createCell(colIndex);
          String type = propertyTypeMap.get(prop);
          String format = propertyFormatMap.get(prop);

          switch (type) {
            case "number":
            case "integer":
              if (value instanceof Number) cell.setCellValue(((Number) value).doubleValue());
              break;
            case "boolean":
              if (value instanceof Boolean) cell.setCellValue((Boolean) value);
              break;
            case "array":
              if (value instanceof List<?> list) {
                String joined = String.join(", ", list.stream().map(String::valueOf).toList());
                cell.setCellValue(joined);
              }
              break;
            case "string":
            default:
              if ("date".equals(format) || "date-time".equals(format)) {
                try {
                  java.text.DateFormat df =
                      "date".equals(format) ? new java.text.SimpleDateFormat("yyyy-MM-dd")
                          : new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                  Date parsed = df.parse(value.toString());
                  cell.setCellValue(parsed);
                  cell.setCellStyle("date".equals(format) ? dateStyle : dateTimeStyle);
                } catch (Exception e) {
                  cell.setCellValue(value.toString());
                }
              } else {
                // Check if this property has enum mapping
                Map<String, String> valueToName = enumValueToNameMap.get(prop);
                if (valueToName != null && valueToName.containsKey(value.toString())) {
                  cell.setCellValue(valueToName.get(value.toString()));
                } else {
                  cell.setCellValue(value.toString());
                }
              }
          }

          colIndex++;
        }
      }

      workbook.write(out);
      return out.toByteArray();
    }
  }

  public static <J extends JsfPOJO<P>, P> List<J> processJsonDataToEntities(
      Map<Integer, Map<String, Object>> indexedJsonData, AlertMessages alertMessages,
      Validator validator, Supplier<J> jsfPojoSupplier,
      Function<Map<String, Object>, P> pojoMapper) {
    var entites = new ArrayList<J>();

    indexedJsonData.entrySet().forEach(e -> {
      var item = jsfPojoSupplier.get();
      var pojo = pojoMapper.apply(e.getValue());
      item.setPojoWithPopulation(pojo);
      entites.add(item);

      var violations = validator.validate(item);
      if (violations.size() > 0) {
        alertMessages.getWarning()
            .add("Row " + (e.getKey() + 1) + " -> " + violations.iterator().next().getMessage());
      }
    });

    return entites;
  }

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

      // Create main data entry sheet
      XSSFSheet sheet = workbook.createSheet("Data Entry");
      XSSFSheet enumSheet = workbook.createSheet("Enums");
      XSSFSheet legendSheet = workbook.createSheet("Legend");
      workbook.setSheetHidden(workbook.getSheetIndex(enumSheet), true);

      // Setup for main sheet
      CreationHelper createHelper = workbook.getCreationHelper();
      Drawing<?> drawing = sheet.createDrawingPatriarch();
      DataValidationHelper helper = sheet.getDataValidationHelper();

      // Create date styles
      CellStyle dateStyle = workbook.createCellStyle();
      dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

      CellStyle dateTimeStyle = workbook.createCellStyle();
      dateTimeStyle
          .setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd\"T\"HH:mm:ss"));

      // Create header rows
      XSSFRow labelRow = sheet.createRow(0); // Field labels (visible)
      XSSFRow originRow = sheet.createRow(1); // Field origins (hidden)
      XSSFRow statusRow = sheet.createRow(2); // Condition status (hidden)
      XSSFRow inputRow = sheet.createRow(3); // User input row

      // Hide metadata rows
      originRow.setZeroHeight(true);
      statusRow.setZeroHeight(true);

      JsonNode properties = flattenedSchema.get("properties");
      if (properties == null || !properties.isObject()) {
        throw new IllegalArgumentException(
            "Invalid JSON Schema: 'properties' is missing or not an object.");
      }

      Map<String, Integer> fieldColumnMap = new HashMap<>();
      Map<String, String> propertyFormatMap = new HashMap<>();
      int colIndex = 0;
      int enumColIndex = 0;

      // Process each property
      for (Iterator<String> it = properties.fieldNames(); it.hasNext();) {
        String propName = it.next();
        JsonNode prop = properties.get(propName);

        fieldColumnMap.put(propName, colIndex);

        // Set field label
        String title = prop.has("title") ? prop.get("title").asText() : propName;
        labelRow.createCell(colIndex).setCellValue(title);

        // Set field origin
        FieldOrigin origin = flattenResult.getFieldOrigins().get(propName);
        originRow.createCell(colIndex).setCellValue(origin != null ? origin.name() : "UNKNOWN");

        // Set condition status formula if field has dependencies
        Cell statusCell = statusRow.createCell(colIndex);
        List<ConditionalDependency> deps = flattenResult.getDependenciesForField(propName);
        if (!deps.isEmpty()) {
          // For now, use the first dependency
          ConditionalDependency dep = deps.get(0);
          Integer condFieldCol = fieldColumnMap.get(dep.getConditionField());
          if (condFieldCol != null) {
            String formula =
                dep.toExcelFormula(CellReference.convertNumToColString(condFieldCol), 4);
            statusCell.setCellFormula(formula);
          } else {
            statusCell.setCellValue("INACTIVE");
          }
        } else {
          statusCell.setCellValue("ACTIVE");
        }

        // Add description as comment
        if (prop.has("description")) {
          ClientAnchor anchor = createHelper.createClientAnchor();
          anchor.setCol1(colIndex);
          anchor.setCol2(colIndex + 2);
          anchor.setRow1(0);
          anchor.setRow2(3);

          Comment comment = drawing.createCellComment(anchor);
          comment.setString(createHelper.createRichTextString(prop.get("description").asText()));
          comment.setAuthor("Schema");
          labelRow.getCell(colIndex).setCellComment(comment);
        }

        // Track format
        if (prop.has("format")) {
          propertyFormatMap.put(propName, prop.get("format").asText());
        }

        // Handle enums
        if (prop.has("enum")) {
          List<String> values = new ArrayList<>();
          JsonNode source = prop.has("enumNames") ? prop.get("enumNames") : prop.get("enum");
          for (JsonNode v : source)
            values.add(v.asText());

          for (int i = 0; i < values.size(); i++) {
            Row row = enumSheet.getRow(i);
            if (row == null) row = enumSheet.createRow(i);
            row.createCell(enumColIndex).setCellValue(values.get(i));
          }

          String rangeName = "Enum_" + propName;
          XSSFName namedRange = workbook.createName();
          namedRange.setNameName(rangeName);
          String colLetter = CellReference.convertNumToColString(enumColIndex);
          String reference = "Enums!$" + colLetter + "$1:$" + colLetter + "$" + values.size();
          namedRange.setRefersToFormula(reference);

          CellRangeAddressList addressList = new CellRangeAddressList(3, 100, colIndex, colIndex);
          DataValidationConstraint constraint = helper.createFormulaListConstraint(rangeName);
          DataValidation validation = helper.createValidation(constraint, addressList);
          validation.setShowErrorBox(true);
          sheet.addValidationData(validation);

          enumColIndex++;
        }

        // Format input cell
        Cell inputCell = inputRow.createCell(colIndex);
        String format = propertyFormatMap.get(propName);
        if ("date".equals(format)) {
          inputCell.setCellStyle(dateStyle);
        } else if ("date-time".equals(format)) {
          inputCell.setCellStyle(dateTimeStyle);
        }

        colIndex++;
      }

      // Apply conditional formatting based on field origins and status
      applyConditionalFormatting(sheet, fieldColumnMap.size());

      // Create legend sheet
      createLegendSheet(legendSheet, workbook);

      // Auto-size columns
      for (int i = 0; i < fieldColumnMap.size(); i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(out);
      return out.toByteArray();
    }
  }

  private static void applyConditionalFormatting(XSSFSheet sheet, int columnCount) {
    SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();

    // Define the range for all data columns in the label row
    CellRangeAddress[] labelRange =
        new CellRangeAddress[] {new CellRangeAddress(0, 0, 0, columnCount - 1)};

    // Rule 1: ORIGINAL_REQUIRED → Blue background
    ConditionalFormattingRule requiredRule = sheetCF
        .createConditionalFormattingRule("INDIRECT(ADDRESS(2,COLUMN(),4))=\"ORIGINAL_REQUIRED\"");
    PatternFormatting requiredFmt = requiredRule.createPatternFormatting();
    requiredFmt.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.index);
    requiredFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
    sheetCF.addConditionalFormatting(labelRange, requiredRule);

    // Rule 2: ALL_OF + ACTIVE → Violet background
    ConditionalFormattingRule allOfRule = sheetCF.createConditionalFormattingRule(
        "AND(INDIRECT(ADDRESS(2,COLUMN(),4))=\"ALL_OF\",INDIRECT(ADDRESS(3,COLUMN(),4))=\"ACTIVE\")");
    PatternFormatting allOfFmt = allOfRule.createPatternFormatting();
    allOfFmt.setFillBackgroundColor(IndexedColors.VIOLET.index);
    allOfFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
    sheetCF.addConditionalFormatting(labelRange, allOfRule);

    // Rule 3: THEN_BRANCH + ACTIVE → Light Turquoise
    ConditionalFormattingRule thenRule = sheetCF.createConditionalFormattingRule(
        "AND(INDIRECT(ADDRESS(2,COLUMN(),4))=\"THEN_BRANCH\",INDIRECT(ADDRESS(3,COLUMN(),4))=\"ACTIVE\")");
    PatternFormatting thenFmt = thenRule.createPatternFormatting();
    thenFmt.setFillBackgroundColor(IndexedColors.LIGHT_TURQUOISE.index);
    thenFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
    sheetCF.addConditionalFormatting(labelRange, thenRule);

    // Rule 4: ELSE_BRANCH + ACTIVE → Grey
    ConditionalFormattingRule elseRule = sheetCF.createConditionalFormattingRule(
        "AND(INDIRECT(ADDRESS(2,COLUMN(),4))=\"ELSE_BRANCH\",INDIRECT(ADDRESS(3,COLUMN(),4))=\"ACTIVE\")");
    PatternFormatting elseFmt = elseRule.createPatternFormatting();
    elseFmt.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.index);
    elseFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
    sheetCF.addConditionalFormatting(labelRange, elseRule);

    // Rule 5: ANY_OF/ONE_OF → Pink when active
    ConditionalFormattingRule anyOneOfRule = sheetCF.createConditionalFormattingRule(
        "AND(OR(INDIRECT(ADDRESS(2,COLUMN(),4))=\"ANY_OF\",INDIRECT(ADDRESS(2,COLUMN(),4))=\"ONE_OF\"),INDIRECT(ADDRESS(3,COLUMN(),4))=\"ACTIVE\")");
    PatternFormatting anyOneOfFmt = anyOneOfRule.createPatternFormatting();
    anyOneOfFmt.setFillBackgroundColor(IndexedColors.PINK.index);
    anyOneOfFmt.setFillPattern(PatternFormatting.SOLID_FOREGROUND);
    sheetCF.addConditionalFormatting(labelRange, anyOneOfRule);
  }

  private static void createLegendSheet(XSSFSheet legendSheet, XSSFWorkbook workbook) {
    // Create styles for legend
    CellStyle headerStyle = workbook.createCellStyle();
    headerStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle blueStyle = workbook.createCellStyle();
    blueStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.index);
    blueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle violetStyle = workbook.createCellStyle();
    violetStyle.setFillForegroundColor(IndexedColors.VIOLET.index);
    violetStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle turquoiseStyle = workbook.createCellStyle();
    turquoiseStyle.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.index);
    turquoiseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle greyStyle = workbook.createCellStyle();
    greyStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
    greyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    CellStyle pinkStyle = workbook.createCellStyle();
    pinkStyle.setFillForegroundColor(IndexedColors.PINK.index);
    pinkStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

    // Create legend content
    Row headerRow = legendSheet.createRow(0);
    Cell headerCell = headerRow.createCell(0);
    headerCell.setCellValue("Field Color Legend");
    headerCell.setCellStyle(headerStyle);
    legendSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

    // Legend entries
    int rowNum = 2;

    Row row1 = legendSheet.createRow(rowNum++);
    Cell colorCell1 = row1.createCell(0);
    colorCell1.setCellStyle(blueStyle);
    row1.createCell(1).setCellValue("Required Field");
    row1.createCell(2).setCellValue("Original schema required field");

    Row row2 = legendSheet.createRow(rowNum++);
    // Cell colorCell2 = row2.createCell(0);
    row2.createCell(1).setCellValue("Optional Field");
    row2.createCell(2).setCellValue("Original schema optional field (no color)");

    Row row3 = legendSheet.createRow(rowNum++);
    Cell colorCell3 = row3.createCell(0);
    colorCell3.setCellStyle(violetStyle);
    row3.createCell(1).setCellValue("All Of Field");
    row3.createCell(2).setCellValue("Field from allOf statement (when active)");

    Row row4 = legendSheet.createRow(rowNum++);
    Cell colorCell4 = row4.createCell(0);
    colorCell4.setCellStyle(turquoiseStyle);
    row4.createCell(1).setCellValue("Then Branch Field");
    row4.createCell(2).setCellValue("Field from if-then condition (when condition is true)");

    Row row5 = legendSheet.createRow(rowNum++);
    Cell colorCell5 = row5.createCell(0);
    colorCell5.setCellStyle(greyStyle);
    row5.createCell(1).setCellValue("Else Branch Field");
    row5.createCell(2).setCellValue("Field from if-else condition (when condition is false)");

    Row row6 = legendSheet.createRow(rowNum++);
    Cell colorCell6 = row6.createCell(0);
    colorCell6.setCellStyle(pinkStyle);
    row6.createCell(1).setCellValue("Any/One Of Field");
    row6.createCell(2).setCellValue("Field from anyOf/oneOf statement (when active)");

    // Instructions
    Row instrRow = legendSheet.createRow(rowNum + 2);
    instrRow.createCell(0).setCellValue("Instructions:");

    Row instr1 = legendSheet.createRow(rowNum + 3);
    instr1.createCell(0).setCellValue("- Field colors change dynamically based on your input");

    Row instr2 = legendSheet.createRow(rowNum + 4);
    instr2.createCell(0)
        .setCellValue("- Conditional fields appear/disappear based on if-then-else conditions");

    Row instr3 = legendSheet.createRow(rowNum + 5);
    instr3.createCell(0)
        .setCellValue("- Hidden rows 2-3 contain metadata for conditional formatting");

    // Auto-size columns
    legendSheet.autoSizeColumn(0);
    legendSheet.autoSizeColumn(1);
    legendSheet.autoSizeColumn(2);
  }

}
