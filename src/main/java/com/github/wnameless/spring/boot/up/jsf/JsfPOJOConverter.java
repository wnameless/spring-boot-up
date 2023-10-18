package com.github.wnameless.spring.boot.up.jsf;

import org.modelmapper.ModelMapper;
import com.github.wnameless.spring.boot.up.model.MapModelConverter;

public interface JsfPOJOConverter<P, J extends JsfPOJO<P, ?>> extends MapModelConverter<P, J> {

  @Override
  default void map(P source, J target) {
    if (beforeMapping() != null) beforeMapping().accept(target);
    J converted = convert(source);
    ModelMapper modelMapper = JsfConfig.getModelMapper();
    P pojo = target.getPojo();
    modelMapper.map(converted, target);
    target.setPojo(pojo);
    if (afterMapping() != null) afterMapping().accept(target);
  }

}
