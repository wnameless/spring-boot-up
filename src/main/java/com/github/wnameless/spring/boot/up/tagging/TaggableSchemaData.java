package com.github.wnameless.spring.boot.up.tagging;

import java.util.List;
import lombok.Data;

@Data
public class TaggableSchemaData<ID> {

  private List<ID> labelList;
  private List<ID> userLabelList;

}
