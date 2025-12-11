package com.github.wnameless.spring.boot.up.model;

import java.util.function.Consumer;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;

@Deprecated(since = "0.18.0")
public interface MapModelConverter<S, T> extends Converter<S, T> {

  default Consumer<T> beforeMapping() {
    return null;
  }

  default void map(S source, T target) {
    if (beforeMapping() != null) beforeMapping().accept(target);
    T converted = convert(source);
    ModelMapper modelMapper = ModelConfig.getModelMapper();
    modelMapper.map(converted, target);
    if (afterMapping() != null) afterMapping().accept(target);
  }

  default Consumer<T> afterMapping() {
    return null;
  }

}
