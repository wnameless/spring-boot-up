package com.github.wnameless.spring.boot.up.tagging;

import java.util.List;
import java.util.Objects;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface Taggable<T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends IdProvider<ID> {

  @SuppressWarnings("unchecked")
  default List<L> getLabelTemplates() {
    TaggingService<T, UL, L, ID> taggingService = SpringBootUp.getBean(TaggingService.class);
    return taggingService.getLabelTemplateRepository()
        .findAllByEntityType(this.getClass().getName());
  }

  @SuppressWarnings("unchecked")
  default List<UL> getUserLabelTemplates() {
    TaggingService<T, UL, L, ID> taggingService = SpringBootUp.getBean(TaggingService.class);
    return taggingService.getUserLabelTemplateRepository().findAllByEntityTypeAndUsername(
        this.getClass().getName(), SpringBootUp.getBean(PermittedUser.class).getUsername());
  }

  @SuppressWarnings("unchecked")
  default List<SystemLabel> getSystemLabels() {
    TaggingService<T, UL, L, ID> taggingService = SpringBootUp.getBean(TaggingService.class);
    return taggingService.getAllSystemLabelsByType(this.getClass());
  }

  @SuppressWarnings("unchecked")
  default List<T> getTagTemplates() {
    TaggingService<T, UL, L, ID> taggingService = SpringBootUp.getBean(TaggingService.class);

    var ownershipRuleOpt = taggingService.findOwnershipRule();
    if (ownershipRuleOpt.isPresent()) {
      return taggingService.getTagTemplateRepository().findAllByEntityId(getId()).stream()
          .filter(ownershipRuleOpt.get()).toList();
    } else {
      return taggingService.getTagTemplateRepository().findAllByEntityId(getId()).stream()
          .filter(tag -> tag.getLabelTemplate() != null || tag.getSystemLabel() != null || Objects
              .equals(tag.getUsername(), SpringBootUp.getBean(PermittedUser.class).getUsername()))
          .toList();
    }
  }

  default boolean isTagEditable() {
    return true;
  }

}
