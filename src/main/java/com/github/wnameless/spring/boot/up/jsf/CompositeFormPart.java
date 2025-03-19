package com.github.wnameless.spring.boot.up.jsf;

import static lombok.AccessLevel.PRIVATE;
import java.util.function.Supplier;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CompositeFormPart {

  @Accessors(fluent = true)
  Supplier<String> formKeyStock;
  @Accessors(fluent = true)
  Supplier<String> formTypeStock;
  @Accessors(fluent = true)
  Supplier<String> formBranchStock;

  public CompositeFormPart(Supplier<String> formTypeStock) {
    this.formKeyStock = formTypeStock;
    this.formTypeStock = formTypeStock;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
  }

  public CompositeFormPart(Supplier<String> formKeyStock, Supplier<String> formTypeStock) {
    this.formKeyStock = formKeyStock;
    this.formTypeStock = formTypeStock;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
  }

  public CompositeFormPart(Supplier<String> formKeyStock, Supplier<String> formTypeStock,
      Supplier<String> formBranchStock) {
    this.formKeyStock = formKeyStock;
    this.formTypeStock = formTypeStock;
    this.formBranchStock = formBranchStock;
  }

}
