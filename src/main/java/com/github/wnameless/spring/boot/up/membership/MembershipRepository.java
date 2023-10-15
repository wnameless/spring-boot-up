package com.github.wnameless.spring.boot.up.membership;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

@NoRepositoryBean
public interface MembershipRepository<OUM extends Membership<R, ID>, R extends Rolify, ID>
    extends CrudRepository<OUM, ID> {

  Optional<OUM> findByUsername(String username);

  List<OUM> findAllByRolesIn(Collection<R> roles);

}
