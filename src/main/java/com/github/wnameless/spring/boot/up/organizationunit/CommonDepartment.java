package com.github.wnameless.spring.boot.up.organizationunit;

import static com.google.common.base.CaseFormat.*;
import org.apache.commons.lang3.StringUtils;

public enum CommonDepartment implements DepartmentNameProvider {

  CHAIRMAN_OFFICE("Chairman's Office"), //
  ADMINISTRATIVE_DEPARTMENT, //
  FINANCE_DEPARTMENT, //
  GENERAL_AFFAIRS_DEPARTMENT, //
  PROCUREMENT_DEPARTMENT, //
  HUMAN_RESOURCES_DEPARTMENT, //
  AUDITORIAL_ROOM, //
  RD_DEPARTMENT("Research & Development Department"), //
  IT_DEPARTMENT("IT Department"), //
  COMPUTER_CENTER, //
  MARKETING_DEPARTMENT, //
  PLANNING_DEPARTMENT, //
  QUALITY_CONTROL_DEPARTMENT, //
  SALES_DEPARTMENT, //
  CUSTOMER_SERVICE_DEPARTMENT;

  private String departmentName;

  private CommonDepartment() {
    var upperCamel = UPPER_UNDERSCORE.to(UPPER_CAMEL, name());
    departmentName = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(upperCamel), ' ');
  }

  private CommonDepartment(String departmentName) {
    this.departmentName = departmentName;
  }

  @Override
  public String getDepartmentName() {
    return departmentName;
  }

}
