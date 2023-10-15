package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Optional;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OrganizationUnitRepository<OU extends OrganizationUnit, ID>
    extends CrudRepository<OU, ID> {

  default Class<?> getResourceType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), CrudRepository.class);
    return genericTypeResolver[0];
  }

  Optional<OU> findByOrganizationUnitName(String organizationUnitName);

}
