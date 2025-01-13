package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;

public class JsfExcelUtils {

  private JsfExcelUtils() {}

  public static void createDropdownWithActualValues(Workbook workbook, Sheet sheet, Cell cell,
      Map<Object, String> enumToNames, String hiddenSheetName) {
    String[] displayNames = enumToNames.values().toArray(String[]::new);
    Object[] actualValues = enumToNames.keySet().toArray();
    createDropdownWithActualValues(workbook, sheet, cell, displayNames, actualValues,
        hiddenSheetName);
  }

  /**
   * Creates a dropdown list in a specified cell using display names, with actual values stored in a
   * hidden sheet. The actual value corresponding to the selected display name is retrieved using
   * VLOOKUP.
   *
   * @param workbook the workbook where the dropdown will be added
   * @param sheet the sheet where the dropdown will be visible
   * @param cell the cell in which to place the dropdown
   * @param displayNames the display names for the dropdown
   * @param actualValues the actual values corresponding to each display name
   * @param hiddenSheetName the name of the hidden sheet that will store the actual values
   */
  public static void createDropdownWithActualValues(Workbook workbook, Sheet sheet, Cell cell,
      String[] displayNames, Object[] actualValues, String hiddenSheetName) {
    // Create or get the hidden sheet
    Sheet hidden = workbook.getSheet(hiddenSheetName);
    if (hidden == null) {
      hidden = workbook.createSheet(hiddenSheetName);
      workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheetName), true);
    }

    // Populate the hidden sheet with display names and actual values
    for (int i = 0; i < displayNames.length; i++) {
      Row row = hidden.getRow(i);
      if (row == null) {
        row = hidden.createRow(i);
      }
      Cell displayNameCell = row.createCell(0);
      displayNameCell.setCellValue(displayNames[i]);
      Cell actualValueCell = row.createCell(1);
      if (actualValues[i] instanceof Number) {
        actualValueCell.setCellValue(Double.valueOf(actualValues[i].toString()));
      } else if (actualValues[i] instanceof Boolean) {
        actualValueCell.setCellValue((Boolean) actualValues[i]);
      } else {
        actualValueCell.setCellValue(actualValues[i].toString());
      }
    }

    // Define named range for the display names
    Name namedRange = workbook.createName();
    String hiddenSheetDisplayNames = hiddenSheetName + "DisplayNames";
    namedRange.setNameName(hiddenSheetDisplayNames);
    namedRange.setRefersToFormula(hiddenSheetName + "!$A$1:$A$" + displayNames.length);

    // Create data validation for dropdown using the named range
    DataValidationHelper validationHelper = sheet.getDataValidationHelper();
    DataValidationConstraint constraint =
        validationHelper.createFormulaListConstraint(hiddenSheetDisplayNames);
    CellRangeAddressList addressList = new CellRangeAddressList(cell.getRowIndex(),
        cell.getRowIndex(), cell.getColumnIndex(), cell.getColumnIndex());
    DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);
    dataValidation.setSuppressDropDownArrow(true);
    sheet.addValidationData(dataValidation);

    // Set a VLOOKUP formula in the adjacent cell to find the actual value
    var row = sheet.getRow(cell.getRowIndex() + 1);
    if (row == null) sheet.createRow(cell.getRowIndex() + 1);
    Cell formulaCell = sheet.getRow(cell.getRowIndex() + 1).createCell(cell.getColumnIndex());
    formulaCell.setCellFormula("VLOOKUP(" + cell.getAddress() + ",'" + hiddenSheetName
        + "'!$A$1:$B$" + actualValues.length + ",2,FALSE)");
  }

}
