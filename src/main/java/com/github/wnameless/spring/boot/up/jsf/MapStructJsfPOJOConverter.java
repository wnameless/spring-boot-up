package com.github.wnameless.spring.boot.up.jsf;

import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct-based converter for JsfPOJO.
 * <p>
 * This interface is designed to work with MapStruct's compile-time code generation. It provides a
 * more efficient and type-safe alternative to the ModelMapper-based {@link JsfPOJOConverter}.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>
 * {@code
 * @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
 * public interface MyConverter extends MapStructJsfPOJOConverter<MyPOJO, MyJsfPOJO> {
 *   // MapStruct will generate the mapToJsfPOJO implementation
 * }
 * }
 * </pre>
 *
 * @param <P> The POJO type (source)
 * @param <J> The JsfPOJO type that extends JsfPOJO&lt;P&gt; (target)
 */
public interface MapStructJsfPOJOConverter<P, J extends JsfPOJO<P>> {

  /**
   * Maps from source POJO to target JsfPOJO.
   * <p>
   * MapStruct will generate the implementation of this method. The {@link MappingTarget} annotation
   * tells MapStruct to update the existing target object instead of creating a new one.
   * </p>
   *
   * @param source The source POJO
   * @param target The target JsfPOJO to be updated
   */
  void mapToJsfPOJO(P source, @MappingTarget J target);

  /**
   * After mapping hook that sets the source POJO on the target.
   * <p>
   * This is automatically called by MapStruct after the field mappings are complete. Subclasses can
   * override this method to add custom post-mapping logic, but should call
   * {@code MapStructJsfPOJOConverter.super.setPojoAfterMapping(source, target)} to ensure the POJO
   * is set.
   * </p>
   *
   * @param source The source POJO
   * @param target The target JsfPOJO
   */
  @AfterMapping
  default void setPojoAfterMapping(P source, @MappingTarget J target) {
    target.setPojo(source);
  }

}
