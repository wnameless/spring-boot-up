package com.github.wnameless.spring.boot.up.tagging;

import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TagTemplateRepository<T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends CrudRepository<T, ID> {

  List<T> findAllByEntityId(ID id);

  List<T> findAllByLabelTemplateInOrUserLabelTemplateIn(Collection<L> labelTemplates,
      Collection<UL> userLabelTemplates);

}
