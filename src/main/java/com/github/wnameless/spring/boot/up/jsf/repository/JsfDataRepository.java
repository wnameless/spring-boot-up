package com.github.wnameless.spring.boot.up.jsf.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;

@NoRepositoryBean
public interface JsfDataRepository<D extends JsfData<S, ID>, S extends JsfSchema<ID>, ID>
    extends CrudRepository<D, ID> {

  D findFirstByJsfSchemaOrderByVersionDesc(S jsfSchema);

}
