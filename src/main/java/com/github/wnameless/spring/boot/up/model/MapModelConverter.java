package com.github.wnameless.spring.boot.up.model;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;

public interface MapModelConverter<S, T> extends Converter<S, T> {

  default void map(S source, T target) {
    T converted = convert(source);
    ModelMapper modelMapper = ModelConfig.getModelMapper();
    modelMapper.map(converted, target);
  }

}
