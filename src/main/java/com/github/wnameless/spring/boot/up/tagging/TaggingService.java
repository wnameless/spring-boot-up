package com.github.wnameless.spring.boot.up.tagging;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import lombok.SneakyThrows;

public interface TaggingService<T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID> {

  LabelTemplateRepository<L, ID> getLabelTemplateRepository();

  UserLabelTemplateRepository<UL, ID> getUserLabelTemplateRepository();

  default List<SystemLabel> getAllSystemLabels() {
    return SpringBootUp.getBeansOfType(SystemLabelTemplate.class).values().stream()
        .map(SystemLabelTemplate::toSystemLabel).toList();
  }

  default List<SystemLabel> getAllSystemLabelsByType(Class<?> entityType) {
    return SpringBootUp.getBeansOfType(SystemLabelTemplate.class).values().stream()
        .filter(st -> st.getEntityTypeByClass().map(entityType::equals).orElse(false))
        .map(SystemLabelTemplate::toSystemLabel).toList();
  }

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

  default List<ID> findAllEntityIdsByLabelIdIn(String entityType,
      @SuppressWarnings("unchecked") ID... labelIds) {
    return findAllEntityIdsByLabelIdIn(entityType, Arrays.asList(labelIds));
  }

  default List<ID> findAllEntityIdsByLabelIdIn(String entityType, Collection<ID> labelIds) {
    var labels = getLabelTemplateRepository().findAllByEntityTypeAndIdIn(entityType, labelIds);
    var userLabels =
        getUserLabelTemplateRepository().findAllByEntityTypeAndIdIn(entityType, labelIds);
    return getTagTemplateRepository()
        .findAllByLabelTemplateInOrUserLabelTemplateInOrSystemLabelIn(labels, userLabels,
            findAllSystemLabelByIds(labelIds.stream().map(Object::toString).toList()))
        .stream().map(TagTemplate::getEntityId).toList();
  }

  default List<ID> findAllEntityIdsByLabelIdIn(String entityType, Collection<ID> labelIds,
      Function<ID, String> systemLabelIdStretagy) {
    var labels = getLabelTemplateRepository().findAllByEntityTypeAndIdIn(entityType, labelIds);
    var userLabels =
        getUserLabelTemplateRepository().findAllByEntityTypeAndIdIn(entityType, labelIds);
    return getTagTemplateRepository()
        .findAllByLabelTemplateInOrUserLabelTemplateInOrSystemLabelIn(labels, userLabels,
            findAllSystemLabelByIds(labelIds.stream().map(systemLabelIdStretagy).toList()))
        .stream().map(TagTemplate::getEntityId).toList();
  }

  default Optional<SystemLabel> findSystemLabelById(String id) {
    return getAllSystemLabels().stream().filter(sl -> Objects.equals(sl.getId(), id)).findFirst();
  }

  default List<SystemLabel> findAllSystemLabelByIds(Collection<String> ids) {
    return getAllSystemLabels().stream().filter(sl -> ids.contains(sl.getId())).toList();
  }

}
