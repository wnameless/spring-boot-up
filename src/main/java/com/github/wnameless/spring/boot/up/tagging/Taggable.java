package com.github.wnameless.spring.boot.up.tagging;

import java.util.List;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.model.DataModelCRUDTrigger;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.web.RestfulItem;

public interface Taggable<I, T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends DataModelCRUDTrigger<I>, RestfulItem<ID> {
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
  default List<T> getTagTemplates() {
    TaggingService<T, UL, L, ID> taggingService = SpringBootUp.getBean(TaggingService.class);
    return taggingService.getTagTemplateRepository().findAllByEntityId(getId());
  }

}
