package com.github.wnameless.spring.boot.up.core;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;

public interface MapModelConverter<S, T> extends Converter<S, T> {

  default void map(S source, T target) {
    T converted = convert(source);
    ModelMapper modelMapper = CoreConfig.getModelMapper();
    modelMapper.map(converted, target);
  }

}
