package com.github.wnameless.spring.boot.up.jsf.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;

@NoRepositoryBean
public interface JsfSchemaRepository<S extends JsfSchema<ID>, ID> extends CrudRepository<S, ID> {

  S findFirstByFormTypeAndFormBranchOrderByVersionDesc(String formType, String formBranch);

}
