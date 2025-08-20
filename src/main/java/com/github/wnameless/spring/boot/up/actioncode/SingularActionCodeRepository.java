package com.github.wnameless.spring.boot.up.actioncode;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SingularActionCodeRepository<AC extends SingularActionCode<A>, A extends Enum<?>, ID>
    extends CrudRepository<AC, ID> {

  Optional<AC> findByActionAndExpiredAtGreaterThan(A action, Instant now);

  Optional<AC> findByActionAndCode(A action, String code);

}
