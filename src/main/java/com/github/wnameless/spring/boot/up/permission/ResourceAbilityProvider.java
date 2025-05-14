package com.github.wnameless.spring.boot.up.permission;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.util.ClassUtils;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.permission.ability.Ability;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import net.sf.rubycollect4j.Ruby;

public interface ResourceAbilityProvider<ID> {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceAbilityProvider.class);

  @SuppressWarnings("unchecked")
  default Optional<ID> findEntityId(Object entity) {
    Class<?> userClass = ClassUtils.getUserClass(entity.getClass());

    Optional<Field> fieldOpt = FieldUtils.getAllFieldsList(userClass).stream()
        .filter(field -> AnnotationUtils.getAnnotation(field, Id.class) != null
            || AnnotationUtils.getAnnotation(field, jakarta.persistence.Id.class) != null)
        .findAny();
    if (fieldOpt.isPresent()) {
      Field field = fieldOpt.get();
      field.setAccessible(true);
      try {
        ID id = (ID) field.get(entity);
        return Optional.ofNullable(id);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        log.error("Id field access failed", e);
      }
    }

    return Optional.empty();
  }

  Set<ResourceAbility> getAllResourceAbilities();

  default WebPermissionManager getWebPermissionManager() {
    return SpringBootUp.getBean(WebPermissionManager.class);
  }

  default Class<?> getResourceType(String resourceName) {
    return getWebPermissionManager().findResourceTypeByName(resourceName).orElse(null);
  }

  default Optional<ResourceAbility> findResourceAbility(Class<?> type, String fieldName,
      Ability... abilities) {
    return getAllResourceAbilities().stream().filter(ra -> {
      return Objects.equals(ra.getResourceType(), ClassUtils.getUserClass(type))
          && Objects.equals(ra.getFieldName(), fieldName) && Ruby.Array.copyOf(abilities)
              .map(Ability::getAbilityName).contains(ra.getAbilityName());
    }).findAny();
  }

  default Optional<ResourceAbility> findResourceAbility(Class<?> type, Ability... abilities) {
    return findResourceAbility(type, null, abilities);
  }

  default boolean existsResourceAbility(Class<?> type, Ability... abilities) {
    return findResourceAbility(type, abilities).isPresent();
  }

}
