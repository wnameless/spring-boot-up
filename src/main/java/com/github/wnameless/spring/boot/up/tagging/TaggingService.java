package com.github.wnameless.spring.boot.up.tagging;

import java.util.List;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import lombok.SneakyThrows;

public interface TaggingService<T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID> {

  LabelTemplateRepository<L, ID> getLabelTemplateRepository();

  UserLabelTemplateRepository<UL, ID> getUserLabelTemplateRepository();

  TagTemplateRepository<T, UL, L, ID> getTagTemplateRepository();

  @SneakyThrows
  @SuppressWarnings("unchecked")
  default L newLabelTemplate() {
    var genericTypeResolver = GenericTypeResolver
        .resolveTypeArguments(getLabelTemplateRepository().getClass(), CrudRepository.class);
    return (L) genericTypeResolver[0].getDeclaredConstructor().newInstance();
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  default T newTagTemplate() {
    var genericTypeResolver = GenericTypeResolver
        .resolveTypeArguments(getTagTemplateRepository().getClass(), CrudRepository.class);
    return (T) genericTypeResolver[0].getDeclaredConstructor().newInstance();
  }

  default List<L> findAllGlobalLabelsByType(Class<?> type) {
    return getLabelTemplateRepository().findAllByEntityType(type.getName());
  }

}
