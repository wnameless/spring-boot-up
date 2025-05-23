package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface OrganizationalUnitRepository<OU extends OrganizationalUnit<ID>, ID>
    extends CrudRepository<OU, ID> {

  @SuppressWarnings("null")
  default Class<?> getResourceType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), CrudRepository.class);
    return genericTypeResolver[0];
  }

  Optional<OU> findByOrganizationalUnitId(ID organizationalUnitId);

  List<OU> findAllByOrganizationalUnitIds(Collection<ID> organizationalUnitIds);

  default List<OU> findAllByOrganizationalUnitName(String organizationalUnitName) {
    throw new UnsupportedOperationException();
  }

}
