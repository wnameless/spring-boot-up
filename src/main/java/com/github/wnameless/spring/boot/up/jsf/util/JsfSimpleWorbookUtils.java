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
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.jsf.JsfPOJO;
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

}
