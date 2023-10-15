package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OrganizationTreeRepository<OT extends OrganizationTree, ID>
    extends CrudRepository<OT, ID> {

  Optional<OT> findByOrganizationName(String organizationName);

}
