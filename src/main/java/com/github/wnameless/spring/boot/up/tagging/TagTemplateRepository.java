package com.github.wnameless.spring.boot.up.tagging;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TagTemplateRepository<T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends CrudRepository<T, ID> {}
