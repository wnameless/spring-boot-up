package com.github.wnameless.spring.boot.up.permission;

import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.CREATE;
import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.CRUD;
import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.DELETE;
import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.MANAGE;
import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.READ;
import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.UPDATE;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ClassUtils;
import com.github.wnameless.spring.boot.up.ApplicationContextProvider;
import com.github.wnameless.spring.boot.up.permission.ability.Ability;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import com.github.wnameless.spring.boot.up.permission.ability.RestAbility;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlAware;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import net.sf.rubycollect4j.Ruby;

public interface PermittedUser<ID> {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PermittedUser.class);

  default String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  Set<Role> getAllRoles();

  default boolean hasRole(String role) {
    return getAllRoles().contains(Role.of(role));
  }

  default boolean hasAllRoles(String... roles) {
    boolean hasAll = true;
    Set<Role> allRoles = getAllRoles();
    for (String role : roles) {
      if (!allRoles.contains(Role.of(role))) return false;
    }
    return hasAll;
  }

  default boolean hasAnyRole(String... roles) {
    boolean hasAny = false;
    Set<Role> allRoles = getAllRoles();
    for (String role : roles) {
      if (allRoles.contains(Role.of(role))) return true;
    }
    return hasAny;
  }

  Set<ResourceAbility> getAllResourceAbilities();

  default WebPermissionManager getWebPermissionManager() {
    ApplicationContext appCtx = ApplicationContextProvider.getApplicationContext();
    return appCtx.getBean(WebPermissionManager.class);
  }

  default Class<?> getResourceType(String resourceName) {
    return getWebPermissionManager().findResourceTypeByName(resourceName);
  }

  @SuppressWarnings("rawtypes")
  default ResourceFilterRepository findResourceFilterRepository(Class<?> type,
      Ability... abilities) {
    Optional<ResourceAbility> raOpt = findResourceAbility(type, abilities);
    if (!raOpt.isPresent()) return null;

    return raOpt.get().getResourceFilterRepository();
  }

  @SuppressWarnings("rawtypes")
  default EmbeddedResourceFilterRepository findEmbeddedResourceFilterRepository(Class<?> type,
      String fieldName, Ability... abilities) {
    Optional<ResourceAbility> raOpt = findResourceAbility(type, fieldName, abilities);
    if (!raOpt.isPresent()) return null;

    return raOpt.get().getEmbeddedResourceFilterRepository();
  }

  default Optional<ResourceAbility> findResourceAbility(Class<?> type, Ability... abilities) {
    return findResourceAbility(type, null, abilities);
  }

  default Optional<ResourceAbility> findResourceAbility(Class<?> type, String fieldName,
      Ability... abilities) {
    return getAllResourceAbilities().stream().filter(ra -> {
      return Objects.equals(ra.getResourceType(), ClassUtils.getUserClass(type))
          && Objects.equals(ra.getFieldName(), fieldName) && Ruby.Array.copyOf(abilities)
              .map(Ability::getAbilityName).contains(ra.getAbilityName());
    }).findAny();
  }

  default boolean existsResourceAbility(Class<?> type, Ability... abilities) {
    return findResourceAbility(type, abilities).isPresent();
  }

  default boolean existsResourceAbility(Class<?> type, String fieldName, Ability... abilities) {
    return findResourceAbility(type, fieldName, abilities).isPresent();
  }

  default boolean canManage(String resourceName, String fieldName) {
    return canManage(getResourceType(resourceName), fieldName);
  }

  default boolean canManage(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, fieldName, MANAGE);
    return ret;
  }

  default boolean canManage(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canManageOn(obj, fieldName, idOpt.get());
    }

    return canManage(obj.getClass(), fieldName);
  }

  default boolean canManageOn(String resourceName, String fieldName, ID id) {
    return canManageOn(getResourceType(resourceName), fieldName, id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canManageOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, MANAGE);
    if (erfr == null) return false;

    EmbeddedResourceAccessRule erar = erfr.getEmbeddedResourceAccessRule();
    boolean ret = erfr.exists(erar.getPredicateOfManageById(id));
    return ret;
  }

  default boolean canManageOn(Object obj, String fieldName, ID id) {
    return canManageOn(obj.getClass(), fieldName, id);
  }

  default boolean canManage(String resourceName) {
    return canManage(getResourceType(resourceName));
  }

  default boolean canManage(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, MANAGE);
    return ret;
  }

  default boolean canManage(Object obj) {
    boolean filterResult;

    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      filterResult = canManageOn(obj, idOpt.get());
    } else {
      filterResult = canManage(obj.getClass());
    }

    return processEntityAccessControl(obj, MANAGE, filterResult);
  }

  default boolean canManageOn(String resourceName, ID id) {
    return canManageOn(getResourceType(resourceName), id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canManageOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    ResourceFilterRepository rfr = findResourceFilterRepository(type, MANAGE);
    if (rfr == null) return false;

    ResourceAccessRule rar = rfr.getResourceAccessRule();
    boolean ret = rfr.exists(rar.getPredicateOfManageById(id));
    return ret;
  }

  default boolean canManageOn(Object obj, ID id) {
    return processEntityAccessControl(obj, MANAGE, canManageOn(obj.getClass(), id));
  }

  default boolean canCRUD(String resourceName, String fieldName) {
    return canCRUD(getResourceType(resourceName), fieldName);
  }

  default boolean canCRUD(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, fieldName, CRUD, MANAGE);
    return ret;
  }

  default boolean canCRUD(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canCRUDOn(obj, fieldName, idOpt.get());
    }

    return canCRUD(obj.getClass(), fieldName);
  }

  default boolean canCRUDOn(String resourceName, String fieldName, ID id) {
    return canCRUDOn(getResourceType(resourceName), fieldName, id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canCRUDOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, CRUD, MANAGE);
    if (erfr == null) return false;

    EmbeddedResourceAccessRule erar = erfr.getEmbeddedResourceAccessRule();
    boolean ret = erfr.exists(erar.getPredicateOfCRUDById(id));
    return ret;
  }

  default boolean canCRUDOn(Object obj, String fieldName, ID id) {
    return canCRUDOn(obj.getClass(), fieldName, id);
  }

  default boolean canCRUD(String resourceName) {
    return canCRUD(getResourceType(resourceName));
  }

  default boolean canCRUD(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, CRUD, MANAGE);
    return ret;
  }

  default boolean canCRUD(Object obj) {
    boolean filterResult;

    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      filterResult = canCRUDOn(obj, idOpt.get());
    } else {
      filterResult = canCRUD(obj.getClass());
    }

    return processEntityAccessControl(obj, CRUD, filterResult);
  }

  default boolean canCRUDOn(String resourceName, ID id) {
    return canCRUDOn(getResourceType(resourceName), id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canCRUDOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    ResourceFilterRepository rfr = findResourceFilterRepository(type, CRUD, MANAGE);
    if (rfr == null) return false;

    ResourceAccessRule rar = rfr.getResourceAccessRule();
    boolean ret = rfr.exists(rar.getPredicateOfCRUDById(id));
    return ret;
  }

  default boolean canCRUDOn(Object obj, ID id) {
    return processEntityAccessControl(obj, CRUD, canCRUDOn(obj.getClass(), id));
  }

  default boolean canCreate(String resourceName, String fieldName) {
    return canCreate(getResourceType(resourceName), fieldName);
  }

  default boolean canCreate(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, fieldName, CREATE, CRUD, MANAGE);
    return ret;
  }

  default boolean canCreate(Object obj, String fieldName) {
    return canCreate(obj.getClass(), fieldName);
  }

  default boolean canCreate(String resourceName) {
    return canCreate(getResourceType(resourceName));
  }

  default boolean canCreate(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, CREATE, CRUD, MANAGE);
    return ret;
  }

  default boolean canCreate(Object obj) {
    return processEntityAccessControl(obj, CREATE, canCreate(obj.getClass()));
  }

  default boolean canRead(String resourceName, String fieldName) {
    return canRead(getResourceType(resourceName), fieldName);
  }

  default boolean canRead(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, fieldName, READ, CRUD, MANAGE);
    return ret;
  }

  default boolean canRead(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canReadOn(obj, fieldName, idOpt.get());
    }

    return canRead(obj.getClass(), fieldName);
  }

  default boolean canReadOn(String resourceName, String fieldName, ID id) {
    return canReadOn(getResourceType(resourceName), fieldName, id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canReadOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, READ, CRUD, MANAGE);
    if (erfr == null) return false;

    EmbeddedResourceAccessRule erar = erfr.getEmbeddedResourceAccessRule();
    boolean ret = erfr.exists(erar.getPredicateOfReadById(id));
    return ret;
  }

  default boolean canReadOn(Object obj, String fieldName, ID id) {
    return canReadOn(obj.getClass(), fieldName, id);
  }

  default boolean canRead(String resourceName) {
    return canRead(getResourceType(resourceName));
  }

  default boolean canRead(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, READ, CRUD, MANAGE);
    return ret;
  }

  default boolean canRead(Object obj) {
    boolean filterResult;

    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      filterResult = canReadOn(obj, idOpt.get());
    } else {
      filterResult = canRead(obj.getClass());
    }

    return processEntityAccessControl(obj, READ, filterResult);
  }

  default boolean canReadOn(String resourceName, ID id) {
    return canReadOn(getResourceType(resourceName), id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canReadOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    ResourceFilterRepository rfr = findResourceFilterRepository(type, READ, CRUD, MANAGE);
    if (rfr == null) return false;

    ResourceAccessRule rar = rfr.getResourceAccessRule();
    boolean ret = rfr.exists(rar.getPredicateOfReadById(id));
    return ret;
  }

  default boolean canReadOn(Object obj, ID id) {
    return processEntityAccessControl(obj, READ, canReadOn(obj.getClass(), id));
  }

  default boolean canUpdate(String resourceName, String fieldName) {
    return canUpdate(getResourceType(resourceName), fieldName);
  }

  default boolean canUpdate(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, fieldName, UPDATE, CRUD, MANAGE);
    return ret;
  }

  default boolean canUpdate(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canUpdateOn(obj, fieldName, idOpt.get());
    }

    return canUpdate(obj.getClass(), fieldName);
  }

  default boolean canUpdateOn(String resourceName, String fieldName, ID id) {
    return canUpdateOn(getResourceType(resourceName), fieldName, id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canUpdateOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, UPDATE, CRUD, MANAGE);
    if (erfr == null) return false;

    EmbeddedResourceAccessRule erar = erfr.getEmbeddedResourceAccessRule();
    boolean ret = erfr.exists(erar.getPredicateOfUpdateById(id));
    return ret;
  }

  default boolean canUpdateOn(Object obj, String fieldName, ID id) {
    return canUpdateOn(obj.getClass(), fieldName, id);
  }

  default boolean canUpdate(String resourceName) {
    return canUpdate(getResourceType(resourceName));
  }

  default boolean canUpdate(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, UPDATE, CRUD, MANAGE);
    return ret;
  }

  default boolean canUpdate(Object obj) {
    boolean filterResult;

    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      filterResult = canUpdateOn(obj, idOpt.get());
    } else {
      filterResult = canUpdate(obj.getClass());
    }

    return processEntityAccessControl(obj, UPDATE, filterResult);
  }

  default boolean canUpdateOn(String resourceName, ID id) {
    return canUpdateOn(getResourceType(resourceName), id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canUpdateOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    ResourceFilterRepository rfr = findResourceFilterRepository(type, UPDATE, CRUD, MANAGE);
    if (rfr == null) return false;

    ResourceAccessRule rar = rfr.getResourceAccessRule();
    boolean ret = rfr.exists(rar.getPredicateOfUpdateById(id));
    return ret;
  }

  default boolean canUpdateOn(Object obj, ID id) {
    return processEntityAccessControl(obj, UPDATE, canUpdateOn(obj.getClass(), id));
  }

  default boolean canDelete(String resourceName, String fieldName) {
    return canDelete(getResourceType(resourceName), fieldName);
  }

  default boolean canDelete(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, fieldName, DELETE, CRUD, MANAGE);
    return ret;
  }

  default boolean canDelete(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canDeleteOn(obj, fieldName, idOpt.get());
    }

    return canDelete(obj.getClass(), fieldName);
  }

  default boolean canDeleteOn(String resourceName, String fieldName, ID id) {
    return canDeleteOn(getResourceType(resourceName), fieldName, id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDeleteOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, DELETE, CRUD, MANAGE);
    if (erfr == null) return false;

    EmbeddedResourceAccessRule erar = erfr.getEmbeddedResourceAccessRule();
    boolean ret = erfr.exists(erar.getPredicateOfDeleteById(id));
    return ret;
  }

  default boolean canDeleteOn(Object obj, String fieldName, ID id) {
    return canDeleteOn(obj.getClass(), fieldName, id);
  }

  default boolean canDelete(String resourceName) {
    return canDelete(getResourceType(resourceName));
  }

  default boolean canDelete(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, DELETE, CRUD, MANAGE);
    return ret;
  }

  default boolean canDelete(Object obj) {
    boolean filterResult;

    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      filterResult = canDeleteOn(obj, idOpt.get());
    } else {
      filterResult = canDelete(obj.getClass());
    }

    return processEntityAccessControl(obj, DELETE, filterResult);
    // return canDelete(obj.getClass());
  }

  default boolean canDeleteOn(String resourceName, ID id) {
    return canDeleteOn(getResourceType(resourceName), id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDeleteOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    ResourceFilterRepository rfr = findResourceFilterRepository(type, DELETE, CRUD, MANAGE);
    if (rfr == null) return false;

    ResourceAccessRule rar = rfr.getResourceAccessRule();
    boolean ret = rfr.exists(rar.getPredicateOfDeleteById(id));
    return ret;
  }

  default boolean canDeleteOn(Object obj, ID id) {
    return processEntityAccessControl(obj, DELETE, canDeleteOn(obj.getClass(), id));
  }

  default boolean canDo(String performAction, String resourceName, String fieldName) {
    return canDo(performAction, getResourceType(resourceName), fieldName);
  }

  default boolean canDo(String performAction, Class<?> type, String fieldName) {
    if (performAction == null) return false;
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, fieldName, Ability.of(performAction), MANAGE);
    return ret;
  }

  default boolean canDo(String performAction, Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canDoOn(performAction, obj, fieldName, idOpt.get());
    }

    return canDo(performAction, obj.getClass(), fieldName);
  }

  default boolean canDoOn(String performAction, String resourceName, String fieldName, ID id) {
    return canDoOn(performAction, getResourceType(resourceName), fieldName, id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDoOn(String performAction, Class<?> type, String fieldName, ID id) {
    if (performAction == null) return false;
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, Ability.of(performAction), MANAGE);
    if (erfr == null) return false;

    EmbeddedResourceAccessRule erar = erfr.getEmbeddedResourceAccessRule();
    boolean ret = erfr.exists(erar.getPredicateOfAbilityById(Ability.of(performAction), id));
    return ret;
  }

  default boolean canDoOn(String performAction, Object obj, String fieldName, ID id) {
    return canDoOn(performAction, obj.getClass(), fieldName, id);
  }

  default boolean canDo(String performAction, String resourceName) {
    return canDo(performAction, getResourceType(resourceName));
  }

  default boolean canDo(String performAction, Class<?> type) {
    if (performAction == null) return false;
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, Ability.of(performAction), MANAGE);
    return ret;
  }

  default boolean canDo(String performAction, Object obj) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canDoOn(performAction, obj, idOpt.get());
    }

    return canDo(performAction, obj.getClass());
  }

  default boolean canDoOn(String performAction, String resourceName, ID id) {
    return canDoOn(performAction, getResourceType(resourceName), id);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDoOn(String performAction, Class<?> type, ID id) {
    if (performAction == null) return false;
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    ResourceFilterRepository rfr =
        findResourceFilterRepository(type, Ability.of(performAction), MANAGE);
    if (rfr == null) return false;

    ResourceAccessRule rar = rfr.getResourceAccessRule();
    boolean ret = rfr.exists(rar.getPredicateOfAbilityById(Ability.of(performAction), id));
    return ret;
  }

  default boolean canDoOn(String performAction, Object obj, ID id) {
    return canDoOn(performAction, obj.getClass(), id);
  }

  Map<String, Set<String>> getUserMetadata();

  default Set<String> getUserMeta(String key) {
    Set<String> meta = getUserMetadata().get(key);
    return meta == null ? new LinkedHashSet<>() : meta;
  }

  @SuppressWarnings("unchecked")
  default Optional<ID> findEntityId(Object entity) {
    Class<?> userClass = ClassUtils.getUserClass(entity.getClass());

    Optional<Field> fieldOpt = Arrays.stream(userClass.getDeclaredFields())
        .filter(field -> AnnotationUtils.getAnnotation(field, Id.class) != null).findAny();
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

  default boolean processEntityAccessControl(Object entity, RestAbility action,
      boolean filterResult) {
    if (entity instanceof AccessControlAware aca) {
      switch (action) {
        case MANAGE:
          if (aca.getManageable().isOverridable()) {
            return aca.getManageable().accessControlRuleStock().getAsBoolean();
          } else {
            return filterResult && aca.getManageable().accessControlRuleStock().getAsBoolean();
          }
        case CRUD:
          if (aca.getCrudable().isOverridable()) {
            return aca.getCrudable().accessControlRuleStock().getAsBoolean();
          } else {
            return filterResult && aca.getCrudable().accessControlRuleStock().getAsBoolean();
          }
        case CREATE:
          if (aca.getCreatable().isOverridable()) {
            return aca.getCreatable().accessControlRuleStock().getAsBoolean();
          } else {
            return filterResult && aca.getCreatable().accessControlRuleStock().getAsBoolean();
          }
        case READ:
          if (aca.getReadable().isOverridable()) {
            return aca.getReadable().accessControlRuleStock().getAsBoolean();
          } else {
            return filterResult && aca.getReadable().accessControlRuleStock().getAsBoolean();
          }
        case UPDATE:
          if (aca.getUpdatable().isOverridable()) {
            return aca.getUpdatable().accessControlRuleStock().getAsBoolean();
          } else {
            return filterResult && aca.getUpdatable().accessControlRuleStock().getAsBoolean();
          }
        case DELETE:
          if (aca.getDeletable().isOverridable()) {
            return aca.getDeletable().accessControlRuleStock().getAsBoolean();
          } else {
            return filterResult && aca.getDeletable().accessControlRuleStock().getAsBoolean();
          }
      }
    }
    return filterResult;
  }

}
