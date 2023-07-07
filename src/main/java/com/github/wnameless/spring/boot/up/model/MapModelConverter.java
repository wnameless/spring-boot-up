package com.github.wnameless.spring.boot.up.model;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import com.github.wnameless.spring.boot.up.jsf.JsfConfig;

public interface MapModelConverter<S, T> extends Converter<S, T> {

  default T map(S source, T target) {
    T converted = convert(source);
    ModelMapper modelMapper = JsfConfig.getModelMapper();
    modelMapper.map(converted, target);
    return target;
  }

}
