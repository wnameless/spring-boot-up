package com.github.wnameless.spring.boot.up.jsf;

import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;

public interface JsfPOJOConverter<P, J extends JsfPOJO<P>> extends Converter<P, J> {

  default void map(P source, J target) {
    J converted = convert(source);
    ModelMapper modelMapper = JsfConfig.getModelMapper();
    P pojo = target.getPojo();
    modelMapper.map(converted, target);
    target.setPojo(pojo);
  }

}
