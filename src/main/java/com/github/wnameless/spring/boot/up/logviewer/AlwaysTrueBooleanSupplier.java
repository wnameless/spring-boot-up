package com.github.wnameless.spring.boot.up.logviewer;

import java.util.function.BooleanSupplier;

public class AlwaysTrueBooleanSupplier implements BooleanSupplier {

  @Override
  public boolean getAsBoolean() {
    return true;
  }

}
