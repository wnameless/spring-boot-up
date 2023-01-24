package com.github.wnameless.spring.boot.up.data.mongodb.cascade;

import static com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeType.ALL;
import static com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeType.DELETE;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.annotation.CascadeRef;

public class CascadeDeleteCallback implements ReflectionUtils.FieldCallback {

  private final Object source;
  private final Set<DeletableId> deletableIds = new LinkedHashSet<DeletableId>();

  public CascadeDeleteCallback(Object source) {
    this.source = source;
  }

  @Override
  public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
    ReflectionUtils.makeAccessible(field);

    if (!field.isAnnotationPresent(DBRef.class) || !field.isAnnotationPresent(CascadeRef.class)) {
      return;
    }

    CascadeRef cascade = AnnotationUtils.getAnnotation(field, CascadeRef.class);
    List<CascadeType> cascadeTypes = Arrays.asList(cascade.value());
    if (!cascadeTypes.contains(ALL) && !cascadeTypes.contains(DELETE)) return;

    Object fieldValue = field.get(source);
    if (fieldValue == null) return;
    // Collection field
    if (Collection.class.isAssignableFrom(fieldValue.getClass())
        || Map.class.isAssignableFrom(fieldValue.getClass())) {
      Collection<?> collection;
      if (Map.class.isAssignableFrom(fieldValue.getClass())) {
        collection = ((Map<?, ?>) fieldValue).values();
      } else {
        collection = (Collection<?>) fieldValue;
      }
      for (Object element : collection) {
        cascadeDeletable(element);
      }
    } else { // Non-Collection field
      cascadeDeletable(fieldValue);
    }
  }

  private void cascadeDeletable(Object value)
      throws IllegalArgumentException, IllegalAccessException {
    IdFieldCallback callback = new IdFieldCallback();
    ReflectionUtils.doWithFields(value.getClass(), callback);

    if (callback.isIdFound() && callback.getId(value) != null) {
      deletableIds.add(DeletableId.of(value.getClass(), callback.getId(value)));
    }
  }

  public Set<DeletableId> getDeletableIds() {
    return deletableIds;
  }

}
