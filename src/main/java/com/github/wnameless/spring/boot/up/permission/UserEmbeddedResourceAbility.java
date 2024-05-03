package com.github.wnameless.spring.boot.up.permission;

import static com.github.wnameless.spring.boot.up.permission.ability.RestAbility.*;
import java.util.Optional;
import org.springframework.util.ClassUtils;
import com.github.wnameless.spring.boot.up.permission.ability.Ability;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceFilterRepository;

public interface UserEmbeddedResourceAbility<ID> extends ResourceAbilityProvider<ID> {

  @SuppressWarnings("rawtypes")
  default EmbeddedResourceFilterRepository findEmbeddedResourceFilterRepository(Class<?> type,
      String fieldName, Ability... abilities) {
    Optional<ResourceAbility> raOpt = findResourceAbility(type, fieldName, abilities);
    if (raOpt.isEmpty()) return null;

    return raOpt.get().getEmbeddedResourceFilterRepository();
  }

  default boolean existsFieldResourceAbility(Class<?> type, String fieldName,
      Ability... abilities) {
    return findResourceAbility(type, fieldName, abilities).isPresent();
  }

  // MANAGE
  default boolean canManageField(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsFieldResourceAbility(type, fieldName, MANAGE);
    return ret;
  }

  default boolean canManageField(String resourceName, String fieldName) {
    return canManageField(getResourceType(resourceName), fieldName);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canManageFieldOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, MANAGE);
    if (erfr == null) return false;

    Optional<EmbeddedResourceAccessRule> erarOpt = erfr.findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return false;
    var erar = erarOpt.get();
    boolean ret = erfr.exists(erar.getPredicateOfManageById(id));
    return ret;
  }

  default boolean canManageFieldOn(String resourceName, String fieldName, ID id) {
    return canManageFieldOn(getResourceType(resourceName), fieldName, id);
  }

  default boolean canManageFieldOn(Object obj, String fieldName, ID id) {
    return canManageFieldOn(obj.getClass(), fieldName, id);
  }

  default boolean canManageField(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canManageFieldOn(obj, fieldName, idOpt.get());
    }

    return canManageField(obj.getClass(), fieldName);
  }

  // CRUD
  default boolean canCRUDField(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsFieldResourceAbility(type, fieldName, CRUD, MANAGE);
    return ret;
  }

  default boolean canCRUDField(String resourceName, String fieldName) {
    return canCRUDField(getResourceType(resourceName), fieldName);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canCRUDFieldOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, CRUD, MANAGE);
    if (erfr == null) return false;

    Optional<EmbeddedResourceAccessRule> erarOpt = erfr.findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return false;
    var erar = erarOpt.get();
    boolean ret = erfr.exists(erar.getPredicateOfCRUDById(id));
    return ret;
  }

  default boolean canCRUDFieldOn(String resourceName, String fieldName, ID id) {
    return canCRUDFieldOn(getResourceType(resourceName), fieldName, id);
  }

  default boolean canCRUDFieldOn(Object obj, String fieldName, ID id) {
    return canCRUDFieldOn(obj.getClass(), fieldName, id);
  }

  default boolean canCRUDField(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canCRUDFieldOn(obj, fieldName, idOpt.get());
    }

    return canCRUDField(obj.getClass(), fieldName);
  }

  // CREATE
  default boolean canCreateField(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsFieldResourceAbility(type, fieldName, CREATE, CRUD, MANAGE);
    return ret;
  }

  default boolean canCreateField(String resourceName, String fieldName) {
    return canCreateField(getResourceType(resourceName), fieldName);
  }

  default boolean canCreateField(Object obj, String fieldName) {
    return canCreateField(obj.getClass(), fieldName);
  }

  // READ
  default boolean canReadField(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsFieldResourceAbility(type, fieldName, READ, CRUD, MANAGE);
    return ret;
  }

  default boolean canReadField(String resourceName, String fieldName) {
    return canReadField(getResourceType(resourceName), fieldName);
  }

  default boolean canReadFieldOn(String resourceName, String fieldName, ID id) {
    return canReadOn(getResourceType(resourceName), fieldName, id);
  }

  default boolean canReadFieldOn(Object obj, String fieldName, ID id) {
    return canReadOn(obj.getClass(), fieldName, id);
  }

  default boolean canReadField(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canReadFieldOn(obj, fieldName, idOpt.get());
    }

    return canReadField(obj.getClass(), fieldName);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canReadOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, READ, CRUD, MANAGE);
    if (erfr == null) return false;

    Optional<EmbeddedResourceAccessRule> erarOpt = erfr.findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return false;
    var erar = erarOpt.get();
    boolean ret = erfr.exists(erar.getPredicateOfReadById(id));
    return ret;
  }

  // UPDATE
  default boolean canUpdateField(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsFieldResourceAbility(type, fieldName, UPDATE, CRUD, MANAGE);
    return ret;
  }

  default boolean canUpdateField(String resourceName, String fieldName) {
    return canUpdateField(getResourceType(resourceName), fieldName);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canUpdateFieldOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, UPDATE, CRUD, MANAGE);
    if (erfr == null) return false;

    Optional<EmbeddedResourceAccessRule> erarOpt = erfr.findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return false;
    var erar = erarOpt.get();
    boolean ret = erfr.exists(erar.getPredicateOfUpdateById(id));
    return ret;
  }

  default boolean canUpdateFieldOn(String resourceName, String fieldName, ID id) {
    return canUpdateFieldOn(getResourceType(resourceName), fieldName, id);
  }

  default boolean canUpdateFieldOn(Object obj, String fieldName, ID id) {
    return canUpdateFieldOn(obj.getClass(), fieldName, id);
  }

  default boolean canUpdateField(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canUpdateFieldOn(obj, fieldName, idOpt.get());
    }

    return canUpdateField(obj.getClass(), fieldName);
  }

  // DELETE
  default boolean canDeleteField(Class<?> type, String fieldName) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsFieldResourceAbility(type, fieldName, DELETE, CRUD, MANAGE);
    return ret;
  }

  default boolean canDeleteField(String resourceName, String fieldName) {
    return canDeleteField(getResourceType(resourceName), fieldName);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDeleteFieldOn(Class<?> type, String fieldName, ID id) {
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, DELETE, CRUD, MANAGE);
    if (erfr == null) return false;

    Optional<EmbeddedResourceAccessRule> erarOpt = erfr.findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return false;
    var erar = erarOpt.get();
    boolean ret = erfr.exists(erar.getPredicateOfDeleteById(id));
    return ret;
  }

  default boolean canDeleteFieldOn(String resourceName, String fieldName, ID id) {
    return canDeleteFieldOn(getResourceType(resourceName), fieldName, id);
  }

  default boolean canDeleteFieldOn(Object obj, String fieldName, ID id) {
    return canDeleteFieldOn(obj.getClass(), fieldName, id);
  }

  default boolean canDeleteField(Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canDeleteFieldOn(obj, fieldName, idOpt.get());
    }

    return canDeleteField(obj.getClass(), fieldName);
  }

  // Do
  default boolean canDoForField(String performAction, Class<?> type, String fieldName) {
    if (performAction == null) return false;
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    boolean ret = existsFieldResourceAbility(type, fieldName, Ability.of(performAction), MANAGE);
    return ret;
  }

  default boolean canDo(String performAction, String resourceName, String fieldName) {
    return canDoForField(performAction, getResourceType(resourceName), fieldName);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean canDoForFieldOn(String performAction, Class<?> type, String fieldName, ID id) {
    if (performAction == null) return false;
    if (type == null) return false;
    if (fieldName == null) return false;
    type = ClassUtils.getUserClass(type);

    EmbeddedResourceFilterRepository erfr =
        findEmbeddedResourceFilterRepository(type, fieldName, Ability.of(performAction), MANAGE);
    if (erfr == null) return false;

    Optional<EmbeddedResourceAccessRule> erarOpt = erfr.findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return false;
    var erar = erarOpt.get();
    boolean ret = erfr.exists(erar.getPredicateOfAbilityById(Ability.of(performAction), id));
    return ret;
  }

  default boolean canDoForFieldOn(String performAction, String resourceName, String fieldName,
      ID id) {
    return canDoForFieldOn(performAction, getResourceType(resourceName), fieldName, id);
  }

  default boolean canDoForFieldOn(String performAction, Object obj, String fieldName, ID id) {
    return canDoForFieldOn(performAction, obj.getClass(), fieldName, id);
  }

  default boolean canDoForField(String performAction, Object obj, String fieldName) {
    Optional<ID> idOpt = findEntityId(obj);
    if (idOpt.isPresent()) {
      return canDoForFieldOn(performAction, obj, fieldName, idOpt.get());
    }

    return canDoForField(performAction, obj.getClass(), fieldName);
  }

}
