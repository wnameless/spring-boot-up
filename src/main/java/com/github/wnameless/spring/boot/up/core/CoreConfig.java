package com.github.wnameless.spring.boot.up.core;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public final class CoreConfig {

  private static ModelMapper modelMapper;

  private CoreConfig() {}

  public static ModelMapper getModelMapper() {
    if (modelMapper != null) return modelMapper;

    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setSkipNullEnabled(true)
        .setMatchingStrategy(MatchingStrategies.STANDARD);
    return modelMapper;
  }

  public static void setModelMapper(ModelMapper modelMapper) {
    CoreConfig.modelMapper = modelMapper;
  }

}
