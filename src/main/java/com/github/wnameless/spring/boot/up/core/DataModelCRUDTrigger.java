package com.github.wnameless.spring.boot.up.core;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface DataModelCRUDTrigger<T> {

  default Class<?> markerInterfaceOfRepository() {
    return QueryByExampleExecutor.class;
  }

  @SuppressWarnings("unchecked")
  default T createThisDataModel() {
    Optional<?> crudRepoOpt =
        SpringBootUp.findGenericBean(markerInterfaceOfRepository(), this.getClass());
    CrudRepository<T, ?> crudRepo = (CrudRepository<T, ?>) crudRepoOpt.get();
    return (T) crudRepo.save((T) this);
  }

  @SuppressWarnings("unchecked")
  default T readThisDataModel() {
    return (T) this;
  }

  @SuppressWarnings("unchecked")
  default void updateThisDataModel() {
    Optional<?> crudRepoOpt =
        SpringBootUp.findGenericBean(markerInterfaceOfRepository(), this.getClass());
    CrudRepository<T, ?> crudRepo = (CrudRepository<T, ?>) crudRepoOpt.get();
    crudRepo.save((T) this);
  }

  @SuppressWarnings("unchecked")
  default void deleteThisDataModel() {
    Optional<?> crudRepoOpt =
        SpringBootUp.findGenericBean(markerInterfaceOfRepository(), this.getClass());
    CrudRepository<T, ?> crudRepo = (CrudRepository<T, ?>) crudRepoOpt.get();
    crudRepo.delete((T) this);
  }

}
