package com.github.wnameless.spring.boot.up.permission;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedCaseInsensitiveMap;
import com.github.wnameless.spring.boot.up.permission.ability.Ability;
import com.github.wnameless.spring.boot.up.permission.ability.AccessAbility;
import com.github.wnameless.spring.boot.up.permission.ability.Can;
import com.github.wnameless.spring.boot.up.permission.ability.CanCRUD;
import com.github.wnameless.spring.boot.up.permission.ability.CanCreate;
import com.github.wnameless.spring.boot.up.permission.ability.CanDelete;
import com.github.wnameless.spring.boot.up.permission.ability.CanManage;
import com.github.wnameless.spring.boot.up.permission.ability.CanRead;
import com.github.wnameless.spring.boot.up.permission.ability.CanUpdate;
import com.github.wnameless.spring.boot.up.permission.ability.Do;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import com.github.wnameless.spring.boot.up.permission.ability.RestAbility;
import com.github.wnameless.spring.boot.up.permission.ability.RoleMeta;
import com.github.wnameless.spring.boot.up.permission.ability.RoleMetadata;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;
import com.github.wnameless.spring.boot.up.permission.role.WebRole;
import net.sf.rubycollect4j.Ruby;
import net.sf.rubycollect4j.RubyArray;

public abstract class WebPermissionManagerAdapter<ID> implements WebPermissionManager {

  @Autowired
  protected ApplicationContext ctx;
  @Autowired
  protected PermittedUser<ID> user;

  protected final Map<String, WebRole> webRoles;
  protected final Map<String, Set<AccessAbility>> role2Abilities;
  protected final Map<String, Map<String, Set<String>>> role2Metadata;
  @SuppressWarnings("rawtypes")
  protected final Map<Class<ResourceFilterRepository>, Set<ResourceAccessRule>> repo2Rules;
  @SuppressWarnings("rawtypes")
  protected final Map<Class<EmbeddedResourceFilterRepository>, Set<EmbeddedResourceAccessRule>> repo2EmbeddedRules;
  protected final Map<String, Class<?>> resourceLookup;
  {
    {
      webRoles = new LinkedCaseInsensitiveMap<>();
      role2Abilities = new LinkedCaseInsensitiveMap<>();
      role2Metadata = new LinkedHashMap<>();
      repo2Rules = new HashMap<>();
      repo2EmbeddedRules = new HashMap<>();
      resourceLookup = new LinkedCaseInsensitiveMap<>();
    }
  }

  protected abstract Set<Role> findAllRolesByUsername(String username);

  @SuppressWarnings({"unchecked", "rawtypes"})
  @EventListener(ApplicationReadyEvent.class)
  private void init() {
    for (ResourceAccessRule rar : ctx.getBeansOfType(ResourceAccessRule.class).values()) {
      if (rar instanceof EmbeddedResourceAccessRule) continue;

      Class<ResourceFilterRepository> klass =
          (Class<ResourceFilterRepository>) rar.getResourceFilterRepository().getClass();

      if (!repo2Rules.containsKey(klass)) {
        repo2Rules.put(klass, new HashSet<>());
      }

      repo2Rules.get(klass).add(rar);
      resourceLookup.put(rar.getResourceName(), rar.getResourceType());
    }

    for (EmbeddedResourceAccessRule erar : ctx.getBeansOfType(EmbeddedResourceAccessRule.class)
        .values()) {
      Class<EmbeddedResourceFilterRepository> klass = (Class<EmbeddedResourceFilterRepository>) erar
          .getEmbeddedResourceFilterRepository().getClass();

      if (!repo2EmbeddedRules.containsKey(klass)) {
        repo2EmbeddedRules.put(klass, new HashSet<>());
      }

      repo2EmbeddedRules.get(klass).add(erar);
      resourceLookup.put(erar.getResourceName(), erar.getResourceType());
    }

    for (WebRole wr : ctx.getBeansOfType(WebRole.class).values()) {
      String role = wr.toRole().getRoleName();
      webRoles.put(role, wr);

      // Abilities
      Map<Class<?>, Set<AccessAbility>> roleAbilities = buildAccessAbilities(wr);
      if (!role2Abilities.containsKey(role)) {
        role2Abilities.put(role, new HashSet<>());
      }
      for (Set<AccessAbility> abilities : roleAbilities.values()) {
        role2Abilities.get(role).addAll(abilities);
      }

      // Metadata
      role2Metadata.put(role, buildRoleMetadata(wr));
    }
  }

  private Map<String, Set<String>> buildRoleMetadata(WebRole webRole) {
    Map<String, Set<String>> metadata = new LinkedHashMap<>();

    RoleMetadata roleMetadata =
        AnnotationUtils.findAnnotation(webRole.getClass(), RoleMetadata.class);
    if (roleMetadata != null) {
      for (RoleMeta roleMeta : roleMetadata.value()) {
        if (metadata.containsKey(roleMeta.key())) {
          metadata.get(roleMeta.key()).addAll(Ruby.Set.copyOf(roleMeta.values()));
        } else {
          metadata.put(roleMeta.key(), Ruby.Set.copyOf(roleMeta.values()).toSet());
        }
      }
    } else {
      RoleMeta roleMeta = AnnotationUtils.findAnnotation(webRole.getClass(), RoleMeta.class);
      if (roleMeta != null) {
        if (metadata.containsKey(roleMeta.key())) {
          metadata.get(roleMeta.key()).addAll(Ruby.Set.copyOf(roleMeta.values()));
        } else {
          metadata.put(roleMeta.key(), Ruby.Set.copyOf(roleMeta.values()).toSet());
        }
      }
    }

    return metadata;
  }

  @Override
  public Class<?> findResourceTypeByName(String resourceName) {
    Class<?> type = resourceLookup.get(resourceName);
    return type;
  }

  private Set<AccessAbility> findUserAccessAbilities(Class<?> resourceType,
      boolean isResourceEmbedded) {
    Set<AccessAbility> accessRuleAbilities = new HashSet<>();

    for (Role role : user.getAllRoles()) {
      for (AccessAbility aa : role2Abilities.get(role.getRoleName())) {
        if (Objects.equals(aa.getResourceType(), resourceType)) {
          if (aa.isResourceEmbedded() == isResourceEmbedded) {
            accessRuleAbilities.add(aa);
          }
        }
      }
    }

    return accessRuleAbilities;
  }

  @Override
  public ResourceAccessRule<?, ?, ?> findUserResourceAccessRuleByRepositoryType(
      @SuppressWarnings("rawtypes") Class<? extends ResourceFilterRepository> repo) {
    @SuppressWarnings("rawtypes")
    Set<ResourceAccessRule> rules = repo2Rules.get(repo);
    if (rules != null && !rules.isEmpty()) {
      Set<AccessAbility> abilities =
          findUserAccessAbilities(rules.iterator().next().getResourceType(), false);
      return findHighestOrderResourceAccessRule(rules, abilities);
    }

    return null;
  }

  @SuppressWarnings("rawtypes")
  private ResourceAccessRule findHighestOrderResourceAccessRule(Set<ResourceAccessRule> rules,
      Set<AccessAbility> abilities) {
    List<Class<? extends ResourceAccessRule>> rarTypes =
        Ruby.Array.copyOf(abilities).map(a -> a.getResourceAccessRuleType());

    return Ruby.Array.copyOf(rules).keepIf(r -> rarTypes.contains(r.getClass()))
        .sortBy(r -> r.getRuleOrder()).first();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public EmbeddedResourceAccessRule findUserEmbeddedResourceAccessRuleByRepositoryType(
      Class<? extends EmbeddedResourceFilterRepository> repo) {
    Set<EmbeddedResourceAccessRule> rules = repo2EmbeddedRules.get(repo);
    if (rules != null && !rules.isEmpty()) {
      Set<AccessAbility> abilities =
          findUserAccessAbilities(rules.iterator().next().getResourceType(), true);
      return findHighestOrderEmbeddedResourceAccessRule(rules, abilities);
    }

    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private EmbeddedResourceAccessRule findHighestOrderEmbeddedResourceAccessRule(
      Set<EmbeddedResourceAccessRule> rules, Set<AccessAbility> abilities) {
    List<Class<? extends EmbeddedResourceAccessRule>> erarTypes = Ruby.Array.copyOf(abilities)
        .map(a -> (Class<? extends EmbeddedResourceAccessRule>) a.getResourceAccessRuleType());

    return Ruby.Array.copyOf(rules).keepIf(r -> erarTypes.contains(r.getClass()))
        .sortBy(r -> r.getRuleOrder()).first();
  }

  @Override
  public Set<Role> getUserRoles() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Set<Role> roles = findAllRolesByUsername(username);
    roles.addAll(retrieveMinorRoles(roles));
    return ensureWebRoles(roles);
  }

  private Set<Role> ensureWebRoles(Set<Role> roles) {
    roles.removeIf(role -> {
      WebRole wr = webRoles.get(role.getRoleName());
      return wr == null || !wr.isActive();
    });
    return roles;
  }

  private Set<Role> retrieveMinorRoles(Set<Role> roles) {
    RubyArray<Role> result = Ruby.Array.create();

    for (Role role : roles) {
      if (webRoles.containsKey(role.getRoleName())) {
        result.addAll(webRoles.get(role.getRoleName()).getMinorRoles());
      }
    }

    return result.toSet();
  }

  @Override
  public Set<ResourceAbility> getUserResourceAbilities() {
    return buildResourceAbilities(getUserRoles());
  }

  private Set<ResourceAbility> buildResourceAbilities(Set<Role> roles) {
    Set<ResourceAbility> resourceAbilities = new HashSet<>();

    for (Role role : roles) {
      if (role2Abilities.containsKey(role.getRoleName())) {
        for (AccessAbility aa : role2Abilities.get(role.getRoleName())) {
          if (aa.isResourceEmbedded()) {
            @SuppressWarnings("unchecked")
            EmbeddedResourceFilterRepository<?, ?, ?> erfr =
                (EmbeddedResourceFilterRepository<?, ?, ?>) getEmbeddedResourceAccessRule(
                    (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) aa
                        .getResourceAccessRuleType()).getEmbeddedResourceFilterRepository();

            resourceAbilities.add(ResourceAbility.ofEmbeddedResource(aa.getResourceType(),
                aa.getFieldName(), aa.getAbilityName(), erfr));
          } else {
            ResourceFilterRepository<?, ?> rfr =
                (ResourceFilterRepository<?, ?>) getResourceAccessRule(
                    aa.getResourceAccessRuleType()).getResourceFilterRepository();

            resourceAbilities
                .add(ResourceAbility.ofResource(aa.getResourceType(), aa.getAbilityName(), rfr));
          }
        }
      }
    }

    return resourceAbilities;
  }

  private ResourceAccessRule<?, ?, ?> getResourceAccessRule(
      Class<? extends ResourceAccessRule<?, ?, ?>> klass) {
    return ctx.getBean(klass);
  }

  private EmbeddedResourceAccessRule<?, ?, ?, ?, ?> getEmbeddedResourceAccessRule(
      Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>> klass) {
    return ctx.getBean(klass);
  }

  @Override
  public Set<Role> getAllRoles() {
    return Ruby.Array.copyOf(webRoles.values()).map(WebRole::toRole).toSet();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private Map<Class<?>, Set<AccessAbility>> buildAccessAbilities(Object bean) {
    Map<Class<?>, Set<AccessAbility>> accessAbilities = new HashMap<>();

    CanManage canManage = AnnotationUtils.findAnnotation(bean.getClass(), CanManage.class);
    if (canManage != null) {
      for (Class<? extends ResourceAccessRule> t : canManage.value()) {
        if (!accessAbilities.containsKey(t)) {
          accessAbilities.put(t, new HashSet<>());
        }

        if (EmbeddedResourceAccessRule.class.isAssignableFrom(t)) {
          EmbeddedResourceAccessRule erar = (EmbeddedResourceAccessRule) ctx.getBean(t);
          accessAbilities.get(t)
              .add(AccessAbility.ofEmbeddedResource(ctx.getBean(t).getResourceType(),
                  erar.getEmbeddedResourceFieldName(), RestAbility.MANAGE.getAbilityName(),
                  (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) t));
        } else {
          accessAbilities.get(t)
              .add(AccessAbility.ofResource(ctx.getBean(t).getResourceType(),
                  RestAbility.MANAGE.getAbilityName(),
                  (Class<? extends ResourceAccessRule<?, ?, ?>>) t));
        }
      }
    }

    CanCRUD canCRUD = AnnotationUtils.findAnnotation(bean.getClass(), CanCRUD.class);
    if (canCRUD != null) {
      for (Class<? extends ResourceAccessRule<?, ?, ?>> t : canCRUD.value()) {
        if (!accessAbilities.containsKey(t)) {
          accessAbilities.put(t, new HashSet<>());
        }

        if (EmbeddedResourceAccessRule.class.isAssignableFrom(t)) {
          EmbeddedResourceAccessRule erar = (EmbeddedResourceAccessRule) ctx.getBean(t);
          accessAbilities.get(t)
              .add(AccessAbility.ofEmbeddedResource(ctx.getBean(t).getResourceType(),
                  erar.getEmbeddedResourceFieldName(), RestAbility.CRUD.getAbilityName(),
                  (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) t));
        } else {
          accessAbilities.get(t).add(AccessAbility.ofResource(ctx.getBean(t).getResourceType(),
              RestAbility.CRUD.getAbilityName(), t));
        }
      }
    }

    CanCreate canCreate = AnnotationUtils.findAnnotation(bean.getClass(), CanCreate.class);
    if (canCreate != null) {
      for (Class<? extends ResourceAccessRule> t : canCreate.value()) {
        if (!accessAbilities.containsKey(t)) {
          accessAbilities.put(t, new HashSet<>());
        }

        if (EmbeddedResourceAccessRule.class.isAssignableFrom(t)) {
          EmbeddedResourceAccessRule erar = (EmbeddedResourceAccessRule) ctx.getBean(t);
          accessAbilities.get(t)
              .add(AccessAbility.ofEmbeddedResource(ctx.getBean(t).getResourceType(),
                  erar.getEmbeddedResourceFieldName(), RestAbility.CREATE.getAbilityName(),
                  (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) t));
        } else {
          accessAbilities.get(t)
              .add(AccessAbility.ofResource(ctx.getBean(t).getResourceType(),
                  RestAbility.CREATE.getAbilityName(),
                  (Class<? extends ResourceAccessRule<?, ?, ?>>) t));
        }
      }
    }

    CanRead canRead = AnnotationUtils.findAnnotation(bean.getClass(), CanRead.class);
    if (canRead != null) {
      for (Class<? extends ResourceAccessRule<?, ?, ?>> t : canRead.value()) {
        if (!accessAbilities.containsKey(t)) {
          accessAbilities.put(t, new HashSet<>());
        }

        if (EmbeddedResourceAccessRule.class.isAssignableFrom(t)) {
          EmbeddedResourceAccessRule erar = (EmbeddedResourceAccessRule) ctx.getBean(t);
          accessAbilities.get(t)
              .add(AccessAbility.ofEmbeddedResource(ctx.getBean(t).getResourceType(),
                  erar.getEmbeddedResourceFieldName(), RestAbility.READ.getAbilityName(),
                  (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) t));
        } else {
          accessAbilities.get(t).add(AccessAbility.ofResource(ctx.getBean(t).getResourceType(),
              RestAbility.READ.getAbilityName(), t));
        }
      }
    }

    CanUpdate canUpdate = AnnotationUtils.findAnnotation(bean.getClass(), CanUpdate.class);
    if (canUpdate != null) {
      for (Class<? extends ResourceAccessRule<?, ?, ?>> t : canUpdate.value()) {
        if (!accessAbilities.containsKey(t)) {
          accessAbilities.put(t, new HashSet<>());
        }

        if (EmbeddedResourceAccessRule.class.isAssignableFrom(t)) {
          EmbeddedResourceAccessRule erar = (EmbeddedResourceAccessRule) ctx.getBean(t);
          accessAbilities.get(t)
              .add(AccessAbility.ofEmbeddedResource(ctx.getBean(t).getResourceType(),
                  erar.getEmbeddedResourceFieldName(), RestAbility.UPDATE.getAbilityName(),
                  (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) t));
        } else {
          accessAbilities.get(t).add(AccessAbility.ofResource(ctx.getBean(t).getResourceType(),
              RestAbility.UPDATE.getAbilityName(), t));
        }
      }
    }

    CanDelete canDestroy = AnnotationUtils.findAnnotation(bean.getClass(), CanDelete.class);
    if (canDestroy != null) {
      for (Class<? extends ResourceAccessRule<?, ?, ?>> t : canDestroy.value()) {
        if (!accessAbilities.containsKey(t)) {
          accessAbilities.put(t, new HashSet<>());
        }

        if (EmbeddedResourceAccessRule.class.isAssignableFrom(t)) {
          EmbeddedResourceAccessRule erar = (EmbeddedResourceAccessRule) ctx.getBean(t);
          accessAbilities.get(t)
              .add(AccessAbility.ofEmbeddedResource(ctx.getBean(t).getResourceType(),
                  erar.getEmbeddedResourceFieldName(), RestAbility.DELETE.getAbilityName(),
                  (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) t));
        } else {
          accessAbilities.get(t).add(AccessAbility.ofResource(ctx.getBean(t).getResourceType(),
              RestAbility.DELETE.getAbilityName(), t));
        }
      }
    }

    Can canDo = AnnotationUtils.findAnnotation(bean.getClass(), Can.class);
    if (canDo != null) {
      for (Do can : canDo.value()) {
        for (Class<? extends ResourceAccessRule<?, ?, ?>> t : can.on()) {
          if (!accessAbilities.containsKey(t)) {
            accessAbilities.put(t, new HashSet<>());
          }

          if (EmbeddedResourceAccessRule.class.isAssignableFrom(t)) {
            EmbeddedResourceAccessRule erar = (EmbeddedResourceAccessRule) ctx.getBean(t);
            accessAbilities.get(t)
                .add(AccessAbility.ofEmbeddedResource(ctx.getBean(t).getResourceType(),
                    erar.getEmbeddedResourceFieldName(), Ability.of(can.action()).getAbilityName(),
                    (Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>>) t));
          } else {
            accessAbilities.get(t).add(AccessAbility.ofResource(ctx.getBean(t).getResourceType(),
                Ability.of(can.action()).getAbilityName(), t));
          }
        }
      }
    }

    return accessAbilities;
  }

  @Override
  public Map<String, Set<String>> getUserMetadata() {
    Map<String, Set<String>> metadata = new LinkedHashMap<>();

    for (Rolify role : getUserRoles()) {
      Map<String, Set<String>> roleMeta = role2Metadata.get(role.getRoleName());
      if (roleMeta != null) {
        metadata = Stream.concat(metadata.entrySet().stream(), roleMeta.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (value1, value2) -> {
              value1.addAll(value2);
              return value1;
            }));
      }
    }

    return metadata;
  }



}
