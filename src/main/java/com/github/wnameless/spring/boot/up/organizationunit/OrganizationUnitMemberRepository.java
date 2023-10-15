package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

@NoRepositoryBean
public interface OrganizationUnitMemberRepository<OU extends OrganizationUnit, R extends Rolify, ID>
    extends CrudRepository<OU, ID> {

  Optional<OU> findByUsername();

  List<OU> findAllByRolesIn(Collection<R> roles);

}
