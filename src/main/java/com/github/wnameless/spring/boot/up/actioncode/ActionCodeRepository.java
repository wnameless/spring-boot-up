package com.github.wnameless.spring.boot.up.actioncode;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ActionCodeRepository<AC extends ActionCode<A, T>, A extends Enum<?>, T, ID>
    extends CrudRepository<AC, ID> {

  Optional<AC> findByActionTargetAndActionAndExpiredAtGreaterThan(T actionTarget, A action,
      LocalDateTime now);

  Optional<AC> findByActionAndCode(A action, String code);

}
