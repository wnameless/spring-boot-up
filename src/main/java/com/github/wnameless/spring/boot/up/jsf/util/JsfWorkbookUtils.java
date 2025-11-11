package com.github.wnameless.spring.boot.up.jsf.util;

import static com.github.wnameless.spring.boot.up.jsf.util.JsfFlattenedJsonUtils.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import com.github.wnameless.spring.boot.up.web.WebActionAlertHelper.AlertMessages;
import com.google.common.collect.LinkedHashMultimap;
import com.jayway.jsonpath.DocumentContext;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfWorkbookUtils {

  public static final String DATASHEET_NAME = "DataPoints";

  public static final int DATA_POINT_NAME_COL = 0;
  public static final int DATA_POINT_JSON_PATH_COL = 1;
  public static final int DATA_POINT_TYPE_COL = 3;
  public static final int DATA_POINT_IS_IN_ARRAY_COL = 5;
  public static final int DATA_POINT_REQUIRED_COL = 7;
  public static final int DATA_POINT_IS_ENUM_COL = 8;
  public static final int DATA_POINT_VALUE_START = 9;
  public static final int DATA_POINT_ROW_START = 1;

  public byte[] workbookToBytes(Workbook workbook) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      workbook.write(out);
      return out.toByteArray();
    }
  }

  public byte[] workbookToBase64Bytes(Workbook workbook) throws IOException {
    byte[] bytes = workbookToBytes(workbook);
    return Base64.getEncoder().encode(bytes);
  }

  private boolean containsError(Cell cell) {
    if (cell == null) {
      return false;
    }
    // Check for direct ERROR cell type (e.g., #N/A, #REF!, #VALUE!, etc.)
    if (cell.getCellType() == CellType.ERROR) {
      return true;
    }
    // Check for FORMULA cells that evaluate to ERROR
    if (cell.getCellType() == CellType.FORMULA
        && cell.getCachedFormulaResultType() == CellType.ERROR) {
      return true;
    }
    return false;
  }

  private String getCellValueAsString(Cell cell, Workbook workbook) {
    if (cell == null) {
      return "";
    }
    DataFormatter dataFormatter = new DataFormatter();
    FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
    String cellValue = dataFormatter.formatCellValue(cell, formulaEvaluator);
    return cellValue;
  }

  private String columnIndexToLetter(int columnIndex) {
    StringBuilder result = new StringBuilder();
    while (columnIndex >= 0) {
      result.insert(0, (char) ('A' + columnIndex % 26));
      columnIndex = columnIndex / 26 - 1;
    }
    return result.toString();
  }

  public Map<String, Object> dataWorkbookToMap(Workbook wb, AlertMessages... alertMessages) {
    var map = new LinkedHashMap<String, Object>();

    try {
      int rowCount = DATA_POINT_ROW_START;
      int startCol = DATA_POINT_VALUE_START;
      var sheet = wb.getSheet(DATASHEET_NAME);
      var row = sheet.getRow(rowCount++);
      var lastRow = sheet.getLastRowNum();

      while (rowCount <= lastRow) {
        int currentCol = startCol; // Track current column for error reporting

        try {
          if (row != null //
              && row.getCell(DATA_POINT_JSON_PATH_COL) != null //
              && row.getCell(DATA_POINT_TYPE_COL) != null //
              && row.getCell(DATA_POINT_IS_IN_ARRAY_COL) != null //
              && row.getCell(DATA_POINT_IS_ENUM_COL) != null //
              && row.getCell(startCol) != null //
              && row.getCell(startCol).getCellType() != CellType.BLANK) {
            var jsonPath = row.getCell(DATA_POINT_JSON_PATH_COL).getStringCellValue();
            var type = row.getCell(DATA_POINT_TYPE_COL).getStringCellValue();
            var isArray = row.getCell(DATA_POINT_IS_IN_ARRAY_COL).getBooleanCellValue();
            var isEnum = row.getCell(DATA_POINT_IS_ENUM_COL).getBooleanCellValue();
            if (!jsonPath.isBlank() && !type.isBlank()) {
              var dataRow = isEnum ? sheet.getRow(rowCount++) : row;
              switch (type) {
                case "string" -> {
                  if (isArray) {
                    int idx = 0;
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    while (cell != null && cell.getCellType() != CellType.BLANK
                        && !containsError(cell)) {
                      var value = getCellValueAsString(cell, wb);
                      map.put(jsonPath.replace(".items", "[" + idx++ + "]"), value);
                      cell = dataRow.getCell(currentCol++);
                    }
                  } else {
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    if (!containsError(cell)) {
                      var value = getCellValueAsString(cell, wb);
                      map.put(jsonPath, value);
                    }
                  }
                }
                case "number" -> {
                  if (isArray) {
                    int idx = 0;
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    while (cell != null && cell.getCellType() != CellType.BLANK
                        && !containsError(cell)) {
                      var value = cell.getNumericCellValue();
                      map.put(jsonPath.replace(".items", "[" + idx++ + "]"), value);
                      cell = dataRow.getCell(currentCol++);
                    }
                  } else {
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    if (!containsError(cell)) {
                      var value = cell.getNumericCellValue();
                      map.put(jsonPath, value);
                    }
                  }
                }
                case "integer" -> {
                  if (isArray) {
                    int idx = 0;
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    while (cell != null && cell.getCellType() != CellType.BLANK
                        && !containsError(cell)) {
                      var value = cell.getNumericCellValue();
                      map.put(jsonPath.replace(".items", "[" + idx++ + "]"),
                          Double.valueOf(value).intValue());
                      cell = dataRow.getCell(currentCol++);
                    }
                  } else {
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    if (!containsError(cell)) {
                      var value = cell.getNumericCellValue();
                      map.put(jsonPath, Double.valueOf(value).intValue());
                    }
                  }
                }
                case "boolean" -> {
                  if (isArray) {
                    int idx = 0;
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    while (cell != null && cell.getCellType() != CellType.BLANK
                        && !containsError(cell)) {
                      var value = cell.getBooleanCellValue();
                      map.put(jsonPath.replace(".items", "[" + idx++ + "]"), value);
                      cell = dataRow.getCell(currentCol++);
                    }
                  } else {
                    currentCol = startCol;
                    var cell = dataRow.getCell(currentCol++);
                    if (!containsError(cell)) {
                      var value = cell.getBooleanCellValue();
                      map.put(jsonPath, value);
                    }
                  }
                }
              }

              startCol = DATA_POINT_VALUE_START;
            }
          }
        } catch (Exception e) {
          // Report error with row and column context
          int humanReadableRow = rowCount; // Already incremented, so this is 1-based
          String columnLetter = columnIndexToLetter(currentCol);
          String errorMessage = String.format("Error at row %d, column %s: %s", humanReadableRow,
              columnLetter, e.getMessage());
          List.of(alertMessages).stream().filter(am -> am != null)
              .forEach(alertMessage -> alertMessage.getDanger().add(errorMessage));
          return new LinkedHashMap<>();
        }

        row = sheet.getRow(rowCount++);
      }

      return JsonUnflattener.unflattenAsMap(map);
    } catch (Exception e) {
      // Handle any other exceptions (e.g., sheet not found, etc.)
      String errorMessage = String.format("Error processing workbook: %s", e.getMessage());
      List.of(alertMessages).stream().filter(am -> am != null)
          .forEach(alertMessage -> alertMessage.getDanger().add(errorMessage));
      return new LinkedHashMap<>();
    }
  }

  public XSSFWorkbook toDataCollectionWorkbook(String schemaJson, String uiSchemaJson) {
    var wb = new XSSFWorkbook();
    var sheet = wb.createSheet(DATASHEET_NAME);

    int currentRow = 0;
    int currentCol = 0;

    int hiddenCount = 1;
    String hiddenPrefix = "option";

    var row = sheet.createRow(currentRow++);
    var cell = row.createCell(currentCol++);
    cell.setCellValue("資料欄位名稱");
    cell = row.createCell(currentCol++);
    cell.setCellValue("jsonPath");
    cell = row.createCell(currentCol++);
    cell.setCellValue("資料欄位屬性");
    cell = row.createCell(currentCol++);
    cell.setCellValue("type");
    cell = row.createCell(currentCol++);
    cell.setCellValue("允許多值");
    cell = row.createCell(currentCol++);
    cell.setCellValue("isArray");
    cell = row.createCell(currentCol++);
    cell.setCellValue("必填");
    cell = row.createCell(currentCol++);
    cell.setCellValue("required");
    cell = row.createCell(currentCol++);
    cell.setCellValue("isEnum");
    cell = row.createCell(currentCol++);
    cell.setCellValue("值");
    currentCol = 0;

    var sectionKeys = new HashSet<String>();
    var map = JsonFlattener.flattenAsMap(schemaJson);
    var jsonPaths = new HashSet<String>();
    for (var key : map.keySet()) {
      var jsonPath = key.replaceAll("\\.type$", "");
      jsonPath = List.of(jsonPath.split("\\.")).stream().filter(
          part -> !part.equals("properties") && !part.matches("^(oneOf|anyOf|allOf)\\[\\d+\\]$"))
          .collect(Collectors.joining("."));

      if (key.endsWith(".type") && !map.get(key).equals("object") && !map.get(key).equals("array")
          && jsonPaths.add(jsonPath)) {
        String type = map.get(key).toString();
        var titleKey = key.replaceAll("\\.type$", ".title");
        String title =
            map.get(titleKey) == null ? (String) map.get(titleKey) : map.get(titleKey).toString();

        if (title != null && sectionKeys.isEmpty()) {
          sectionKeys.add(key.split("\\.")[1]);
        } else {
          if (title != null && sectionKeys.add(key.split("\\.")[1])) sheet.createRow(currentRow++);
        }

        if (Strings.isNotBlank(title)) {
          row = sheet.createRow(currentRow++);
          cell = row.createCell(currentCol++);
          cell.setCellValue(title);
          cell = row.createCell(currentCol++);
          cell.setCellValue(jsonPath);
          cell = row.createCell(currentCol++);
          if ("string".equals(type)) {
            cell.setCellValue("文字");
            cell = row.createCell(currentCol++);
            cell.setCellValue("string");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items") ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items"));
            cell = row.createCell(currentCol++);
            cell.setCellValue(
                JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson) ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson));
            cell = row.createCell(currentCol++);
            var enumToNames =
                JsfFlattenedJsonUtils.schemaKeyToEnumToNames(key, schemaJson, uiSchemaJson);
            if (enumToNames.isEmpty()) {
              cell.setCellValue(false);
              cell = row.createCell(currentCol++);
              cell.setCellType(CellType.STRING);
            } else {
              cell.setCellValue(true);
              cell = row.createCell(currentCol++);
              sheet.createRow(currentRow++);
              JsfExcelUtils.createDropdownWithActualValues(wb, sheet, cell, enumToNames,
                  hiddenPrefix + hiddenCount++);
            }
          }
          if ("boolean".equals(type)) {
            cell.setCellValue("布林");
            cell = row.createCell(currentCol++);
            cell.setCellValue("boolean");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items") ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items"));
            cell = row.createCell(currentCol++);
            cell.setCellValue(
                JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson) ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson));
            cell = row.createCell(currentCol++);
            var enumToNames =
                JsfFlattenedJsonUtils.schemaKeyToEnumToNames(key, schemaJson, uiSchemaJson);
            if (enumToNames.isEmpty()) {
              cell.setCellValue(false);
              cell = row.createCell(currentCol++);
              cell.setCellType(CellType.BOOLEAN);
            } else {
              cell.setCellValue(true);
              cell = row.createCell(currentCol++);
              sheet.createRow(currentRow++);
              JsfExcelUtils.createDropdownWithActualValues(wb, sheet, cell, enumToNames,
                  hiddenPrefix + hiddenCount++);
            }
          }
          if ("number".equals(type)) {
            cell.setCellValue("數字");
            cell = row.createCell(currentCol++);
            cell.setCellValue("number");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items") ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items"));
            cell = row.createCell(currentCol++);
            cell.setCellValue(
                JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson) ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson));
            cell = row.createCell(currentCol++);
            var enumToNames =
                JsfFlattenedJsonUtils.schemaKeyToEnumToNames(key, schemaJson, uiSchemaJson);
            if (enumToNames.isEmpty()) {
              cell.setCellValue(false);
              cell = row.createCell(currentCol++);
              cell.setCellType(CellType.NUMERIC);
            } else {
              cell.setCellValue(true);
              cell = row.createCell(currentCol++);
              sheet.createRow(currentRow++);
              JsfExcelUtils.createDropdownWithActualValues(wb, sheet, cell, enumToNames,
                  hiddenPrefix + hiddenCount++);
            }
          }
          if ("integer".equals(type)) {
            cell.setCellValue("整數");
            cell = row.createCell(currentCol++);
            cell.setCellValue("integer");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items") ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(key.contains(".items"));
            cell = row.createCell(currentCol++);
            cell.setCellValue(
                JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson) ? "是" : "");
            cell = row.createCell(currentCol++);
            cell.setCellValue(JsfFlattenedJsonUtils.isSchemaKeyRequired(key, schemaJson));
            cell = row.createCell(currentCol++);
            var enumToNames =
                JsfFlattenedJsonUtils.schemaKeyToEnumToNames(key, schemaJson, uiSchemaJson);
            if (enumToNames.isEmpty()) {
              cell.setCellValue(false);
              cell = row.createCell(currentCol++);
              cell.setCellType(CellType.NUMERIC);
            } else {
              cell.setCellValue(true);
              cell = row.createCell(currentCol++);
              sheet.createRow(currentRow++);
              JsfExcelUtils.createDropdownWithActualValues(wb, sheet, cell, enumToNames,
                  hiddenPrefix + hiddenCount++);
            }
          }

          currentCol = 0;
        }
      }
    }

    for (int i = 0; i < 7; i++) {
      sheet.autoSizeColumn(i);
    }
    // jsonPath
    sheet.setColumnHidden(1, true);
    // type
    sheet.setColumnHidden(3, true);
    // isArray
    sheet.setColumnHidden(5, true);
    // required
    sheet.setColumnHidden(7, true);
    // isEnum
    sheet.setColumnHidden(8, true);

    return wb;
  }

  public XSSFWorkbook toDisplayWorkbook(List<JsfPropertyDetail> propertyDetails) {
    return toDisplayWorkbook(propertyDetails, null, false);
  }

  public XSSFWorkbook toDisplayWorkbook(List<JsfPropertyDetail> propertyDetails,
      Function<Cell, Cell> replacer, boolean hideArrayIndex) {
    var wb = new XSSFWorkbook();
    var sheetName = propertyDetails.stream().filter(pd -> pd.getParentKey().equals("")).findAny()
        .map(pd -> pd.getParentTitle()).orElse("Datasheet");
    var sheet = wb.createSheet(sheetName);

    int currentRow = 0;
    int currentCol = 0;

    int maxColNum = 2;

    var rootKeys = new HashSet<String>();
    while (!propertyDetails.isEmpty()) {
      var pd = propertyDetails.remove(0);

      if (pd.getTitle() == null && !(pd.isInArray() && pd.getKey().matches(".*\\[\\d+\\]$"))) {
        continue;
      }

      if (rootKeys.add(pd.getRootKey())) {
        var row = sheet.createRow(currentRow++);
        var cell = row.createCell(currentCol);
        cell.setCellValue(pd.getRootTitle());
        setBold(24, cell, wb);
        if (pd.isInArray() && pd.getArrayTitle() != null
            && !Objects.equals(pd.getArrayKey(), pd.getRootKey())) {
          row = sheet.createRow(currentRow++);
          cell = row.createCell(currentCol);
          cell.setCellValue(pd.getArrayTitle());
          setBold(16, cell, wb);
          rootKeys.add(pd.getRootKey() + pd.getArrayKey());
        }
        currentCol = 0;
      } else if (pd.isInArray() && rootKeys.add(pd.getRootKey() + pd.getArrayKey())) {
        var row = sheet.createRow(currentRow++);
        var cell = row.createCell(currentCol);
        cell.setCellValue(pd.getArrayTitle());
        setBold(16, cell, wb);
      }

      if (pd.isInArray()) {
        var arrayKey = pd.getArrayKey();
        var itemType = pd.getItemType();
        var pdCache = LinkedHashMultimap.<Integer, JsfPropertyDetail>create();
        pdCache.put(pd.getArrayIndex(), pd);
        while (!propertyDetails.isEmpty() && propertyDetails.get(0).isInArray()
            && arrayKey.equals(propertyDetails.get(0).getArrayKey())) {
          pdCache.put(propertyDetails.get(0).getArrayIndex(), propertyDetails.remove(0));
        }
        var maxItemFields = pdCache.keySet().stream().map(k -> pdCache.get(k).size())
            .mapToInt(Integer::valueOf).max().getAsInt();
        maxColNum = maxItemFields * 2;

        XSSFRow titleRow = null;
        boolean hasItemTitle = pdCache.get(0).iterator().next().getItemTitle() != null;
        if (hasItemTitle) {
          titleRow = sheet.createRow(currentRow++);
        }
        var fieldRows = new ArrayList<XSSFRow>();
        for (int i = 0; i < maxItemFields; i++) {
          if (hideArrayIndex && !hasItemTitle && maxItemFields == 1) {
            fieldRows.add(sheet.getRow(currentRow - 1));
          } else {
            fieldRows.add(sheet.createRow(currentRow++));
          }
        }
        var fieldRowColumnCount = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < pdCache.keySet().size(); i++) {
          fieldRowColumnCount.put(0, 0);

          if (hasItemTitle) {
            var cell = titleRow.createCell(i * 2);
            cell.setCellValue(pdCache.get(0).iterator().next().getItemTitle());
            setBold(16, cell, wb);
          }

          var fields = new ArrayList<>(pdCache.get(i));
          for (int j = 0; j < fields.size(); j++) {
            var fieldRow = fieldRows.get(j);
            XSSFCell name;
            XSSFCell value;
            if (hideArrayIndex) {
              if (!hasItemTitle && maxItemFields == 1) {
                name = fieldRow.createCell(i + 1);
                value = fieldRow.createCell(i + 1);
              } else {
                name = fieldRow.createCell(i);
                value = fieldRow.createCell(i);
              }
            } else {
              name = fieldRow.createCell(i * 2);
              value = fieldRow.createCell(i * 2 + 1);
            }

            if (!hideArrayIndex) {
              if ("object".equals(itemType)) {
                name.setCellValue(fields.get(j).getTitle());
              } else {
                name.setCellValue("" + (i + 1));
              }
            }
            value.setCellValue(fields.get(j).getValue().toString());
            if (replacer != null) replacer.apply(value);
          }
        }
      } else {
        var row = sheet.createRow(currentRow++);
        var cell = row.createCell(currentCol++);
        cell.setCellValue(pd.getTitle());
        cell = row.createCell(currentCol++);
        cell.setCellValue(pd.getValue().toString());
        if (replacer != null) replacer.apply(cell);
      }

      currentCol = 0;
    }

    for (int i = 0; i < maxColNum; i++) {
      sheet.autoSizeColumn(i);
    }
    return wb;
  }

  private static Cell setBold(int fontSize, Cell cell, Workbook workbook) {
    CellStyle style = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setFontHeightInPoints((short) fontSize);
    font.setBold(true);
    style.setFont(font);
    cell.setCellStyle(style);
    return cell;
  }

  public List<JsfPropertyDetail> schemaData2JsfPropertyDetails(Object pojo,
      DocumentContext schemaJson, DocumentContext uiSchemaJson) throws JsonProcessingException {
    var propertyDetails = new ArrayList<JsfPropertyDetail>();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    var json = mapper.writeValueAsString(pojo);

    var map = JsonFlattener.flattenAsMap(json);
    map.entrySet().forEach(e -> {
      propertyDetails
          .add(flattenKey2JsfPropertyDetail(e.getKey(), e.getValue(), schemaJson, uiSchemaJson));
    });

    return propertyDetails;
  }

  public JsfPropertyDetail flattenKey2JsfPropertyDetail(String key, Object value,
      DocumentContext schemaJson, DocumentContext uiSchemaJson) {
    var propertyDetail = new JsfPropertyDetail();

    propertyDetail.setKey(key);
    propertyDetail.setNestedLevel(key.split("\\.").length);

    propertyDetail.setRootKey(keyToRootKey(key));
    propertyDetail.setRootTitle(keyToRootTitle(key, schemaJson, uiSchemaJson));

    propertyDetail.setParentKey(keyToParentKey(key));
    propertyDetail.setParentTitle(keyToParentTitle(key, schemaJson, uiSchemaJson));
    propertyDetail.setParentType(keyToParentType(key, schemaJson));

    propertyDetail.setInArray(isKeyInArray(key));
    if (propertyDetail.isInArray()) {
      propertyDetail.setArrayIndex(getKeyArrayIndex(key));
      propertyDetail.setArrayKey(keyToArrayKey(key));
      propertyDetail.setArrayTitle(keyToArrayTitle(key, schemaJson, uiSchemaJson));
      propertyDetail.setItemTitle(keyToItemTitle(key, schemaJson, uiSchemaJson));
      propertyDetail.setItemType(keyToItemType(key, schemaJson));
    }

    propertyDetail.setEnumToNames(keyToEnumToNames(key, schemaJson, uiSchemaJson));

    propertyDetail.setTitle(keyToTitle(key, schemaJson, uiSchemaJson));
    propertyDetail.setType(keyToType(key, schemaJson));
    if (propertyDetail.getEnumToNames().get(value) != null) {
      propertyDetail.setValue(propertyDetail.getEnumToNames().get(value));
    } else {
      propertyDetail.setValue(value);
    }

    return propertyDetail;
  }

}
