package com.github.wnameless.spring.boot.up.membership;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.permission.role.Role;

@NoRepositoryBean
public interface MembershipRepository<OUM extends Membership<ID>, ID>
    extends CrudRepository<OUM, ID> {

  Optional<OUM> findByUsername(String username);

  List<OUM> findAllByRolesIn(Collection<Role> roles);

}
