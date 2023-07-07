package com.github.wnameless.spring.boot.up.jsf;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;

public interface MapModelConverter<S, T> extends Converter<S, T> {

  default T map(S source, T target) {
    T converted = convert(source);
    ModelMapper modelMapper = JsfConfig.getModelMapper();
    modelMapper.map(converted, target);
    return target;
  }

}
