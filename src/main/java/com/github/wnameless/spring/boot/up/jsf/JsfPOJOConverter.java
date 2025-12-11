package com.github.wnameless.spring.boot.up.jsf;

import org.modelmapper.ModelMapper;
import com.github.wnameless.spring.boot.up.model.MapModelConverter;

/**
 * Legacy POJO to JsfPOJO converter using ModelMapper.
 *
 * @deprecated Use {@link MapStructJsfPOJOConverter} instead for better performance and type safety.
 *             ModelMapper uses runtime reflection which is slower and has known bugs. MapStruct
 *             generates code at compile-time providing better performance and compile-time
 *             validation.
 */
@Deprecated(since = "0.18.0")
public interface JsfPOJOConverter<P, J extends JsfPOJO<P>> extends MapModelConverter<P, J> {

  @Override
  default void map(P source, J target) {
    if (beforeMapping() != null) beforeMapping().accept(target);
    J converted = convert(source);
    ModelMapper modelMapper = JsfConfig.getModelMapper();
    // P pojo = target.getPojo();
    modelMapper.map(converted, target);
    target.setPojo(source);
    if (afterMapping() != null) afterMapping().accept(target);
  }

}
