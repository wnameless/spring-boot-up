package com.github.wnameless.spring.boot.up.model;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public final class ModelConfig {

  private static ModelMapper modelMapper;

  private ModelConfig() {}

  public static ModelMapper getModelMapper() {
    if (modelMapper != null) return modelMapper;

    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setSkipNullEnabled(true)
        .setMatchingStrategy(MatchingStrategies.STANDARD);
    return modelMapper;
  }

  public static void setModelMapper(ModelMapper modelMapper) {
    ModelConfig.modelMapper = modelMapper;
  }

}
