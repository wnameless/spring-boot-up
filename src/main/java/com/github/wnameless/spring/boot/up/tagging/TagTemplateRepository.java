package com.github.wnameless.spring.boot.up.tagging;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TagTemplateRepository<TT extends TagTemplate, ID> extends CrudRepository<TT, ID> {}
