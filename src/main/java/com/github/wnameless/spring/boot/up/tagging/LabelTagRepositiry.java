package com.github.wnameless.spring.boot.up.tagging;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LabelTagRepositiry<LT extends LabelTag<TT, ID>, TT extends TagTemplate, ID>
    extends CrudRepository<LT, ID> {}
