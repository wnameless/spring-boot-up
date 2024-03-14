package com.github.wnameless.spring.boot.up.tagging;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TagTemplateRepository<T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends CrudRepository<T, ID> {

  List<T> findAllByEntityId(ID id);

  Optional<T> findByEntityIdAndSystemLabel(ID id, SystemLabel systemLabel);

  List<T> findAllByLabelTemplateInOrUserLabelTemplateInOrSystemLabelIn(Collection<L> labelTemplates,
      Collection<UL> userLabelTemplates, Collection<SystemLabel> systemLabels);

  List<T> findAllByUsernameAndLabelTemplateInOrUsernameAndUserLabelTemplateInOrUsernameAndSystemLabelIn(
      String username1, Collection<L> labelTemplates, String username2,
      Collection<UL> userLabelTemplates, String username3, Collection<SystemLabel> systemLabels);

}
