package com.github.wnameless.spring.boot.up.permission;

import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.*;
import java.util.Optional;
import org.springframework.util.ClassUtils;
import com.github.wnameless.spring.boot.up.permission.ability.Ability;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import com.github.wnameless.spring.boot.up.permission.ability.RestAbility;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControllable;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;

public interface UserResourceAbility<ID> extends ResourceAbilityProvider<ID> {

  @SuppressWarnings("rawtypes")
  default Optional<ResourceFilterRepository> findResourceFilterRepository(Class<?> type,
      Ability... abilities) {
    Optional<ResourceAbility> raOpt = findResourceAbility(type, abilities);
    return raOpt.map(ResourceAbility::getResourceFilterRepository);
  }

  // Exists
  default boolean existsPermission(Class<?> type) {
    return getWebPermissionManager().existsResourceType(ClassUtils.getUserClass(type));
  }

  default boolean existsPermission(String resourceName) {
    return getWebPermissionManager().findResourceTypeByName(resourceName).isPresent();
  }

  // MANAGE
  default boolean canManage(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, MANAGE);
    return ret;
  }

  default boolean canManage(String resourceName) {
    return canManage(getResourceType(resourceName));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canManageOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    return findResourceFilterRepository(type, MANAGE)
        .filter(rfr -> rfr.findResourceAccessRule().isPresent())
        .map(rfr -> rfr.exists(
            ((ResourceAccessRule) rfr.findResourceAccessRule().get()).getPredicateOfManageById(id)))
        .orElse(false);
  }

  default boolean canManageOn(String resourceName, ID id) {
    return canManageOn(getResourceType(resourceName), id);
  }

  default boolean canManageOn(Object obj, ID id) {
    return processEntityAccessControl(obj, MANAGE, canManageOn(obj.getClass(), id));
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

  // CRUD
  default boolean canCRUD(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, CRUD, MANAGE);
    return ret;
  }

  default boolean canCRUD(String resourceName) {
    return canCRUD(getResourceType(resourceName));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canCRUDOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    return canManageOn(type, id) ? true
        : findResourceFilterRepository(type, CRUD)
            .filter(rfr -> rfr.findResourceAccessRule().isPresent())
            .map(rfr -> rfr.exists(((ResourceAccessRule) rfr.findResourceAccessRule().get())
                .getPredicateOfCRUDById(id)))
            .orElse(false);
  }

  default boolean canCRUDOn(String resourceName, ID id) {
    return canCRUDOn(getResourceType(resourceName), id);
  }

  default boolean canCRUDOn(Object obj, ID id) {
    return processEntityAccessControl(obj, CRUD, canCRUDOn(obj.getClass(), id));
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

  // READ
  default boolean canRead(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, READ, CRUD, MANAGE);
    return ret;
  }

  default boolean canRead(String resourceName) {
    return canRead(getResourceType(resourceName));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canReadOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    return canManageOn(type, id) ? true
        : findResourceFilterRepository(type, READ, CRUD)
            .filter(rfr -> rfr.findResourceAccessRule().isPresent())
            .map(rfr -> rfr.exists(((ResourceAccessRule) rfr.findResourceAccessRule().get())
                .getPredicateOfReadById(id)))
            .orElse(false);
  }

  default boolean canReadOn(String resourceName, ID id) {
    return canReadOn(getResourceType(resourceName), id);
  }

  default boolean canReadOn(Object obj, ID id) {
    return processEntityAccessControl(obj, READ, canReadOn(obj.getClass(), id));
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

  // CREATE
  default boolean canCreate(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, CREATE, CRUD, MANAGE);
    return ret;
  }

  default boolean canCreate(String resourceName) {
    return canCreate(getResourceType(resourceName));
  }

  default boolean canCreate(Object obj) {
    return processEntityAccessControl(obj, CREATE, canCreate(obj.getClass()));
  }

  // UPDATE
  default boolean canUpdate(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, UPDATE, CRUD, MANAGE);
    return ret;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canUpdateOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    return canManageOn(type, id) ? true
        : findResourceFilterRepository(type, UPDATE, CRUD)
            .filter(rfr -> rfr.findResourceAccessRule().isPresent())
            .map(rfr -> rfr.exists(((ResourceAccessRule) rfr.findResourceAccessRule().get())
                .getPredicateOfUpdateById(id)))
            .orElse(false);
  }

  default boolean canUpdate(String resourceName) {
    return canUpdate(getResourceType(resourceName));
  }

  default boolean canUpdateOn(String resourceName, ID id) {
    return canUpdateOn(getResourceType(resourceName), id);
  }

  default boolean canUpdateOn(Object obj, ID id) {
    return processEntityAccessControl(obj, UPDATE, canUpdateOn(obj.getClass(), id));
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

  // DELETE
  default boolean canDelete(Class<?> type) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, DELETE, CRUD, MANAGE);
    return ret;
  }

  default boolean canDelete(String resourceName) {
    return canDelete(getResourceType(resourceName));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDeleteOn(Class<?> type, ID id) {
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    return canManageOn(type, id) ? true
        : findResourceFilterRepository(type, DELETE, CRUD)
            .filter(rfr -> rfr.findResourceAccessRule().isPresent())
            .map(rfr -> rfr.exists(((ResourceAccessRule) rfr.findResourceAccessRule().get())
                .getPredicateOfDeleteById(id)))
            .orElse(false);
  }

  default boolean canDeleteOn(String resourceName, ID id) {
    return canDeleteOn(getResourceType(resourceName), id);
  }

  default boolean canDeleteOn(Object obj, ID id) {
    return processEntityAccessControl(obj, DELETE, canDeleteOn(obj.getClass(), id));
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
  }

  // Do
  default boolean canDo(String performAction, Class<?> type) {
    if (performAction == null) return false;
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsResourceAbility(type, Ability.of(performAction), MANAGE);
    return ret;
  }

  default boolean canDo(String performAction, String resourceName) {
    return canDo(performAction, getResourceType(resourceName));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDoOn(String performAction, Class<?> type, ID id) {
    if (performAction == null) return false;
    if (type == null) return false;
    type = ClassUtils.getUserClass(type);

    return canManageOn(type, id) ? true
        : findResourceFilterRepository(type, Ability.of(performAction))
            .filter(rfr -> rfr.findResourceAccessRule().isPresent())
            .map(rfr -> rfr.exists(((ResourceAccessRule) rfr.findResourceAccessRule().get())
                .getPredicateOfAbilityById(Ability.of(performAction), id)))
            .orElse(false);
  }

  default boolean canDoOn(String performAction, String resourceName, ID id) {
    return canDoOn(performAction, getResourceType(resourceName), id);
  }

  default boolean canDoOn(String performAction, Object obj, ID id) {
    return canDoOn(performAction, obj.getClass(), id);
  }

  default boolean canDo(String performAction, Object obj) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canDoOn(performAction, obj, idOpt.get());
    }

    return canDo(performAction, obj.getClass());
  }

  // AccessControlAware
  default boolean processEntityAccessControl(Object entity, RestAbility action,
      boolean filterResult) {
    if (entity instanceof AccessControllable aca) {
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
