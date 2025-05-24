package com.github.wnameless.spring.boot.up.model;

public interface UserAuditable {

  String getCreatedBy();

  void setCreatedBy(String createdBy);

  String getUpdatedBy();

  void setUpdatedBy(String updatedBy);

}
