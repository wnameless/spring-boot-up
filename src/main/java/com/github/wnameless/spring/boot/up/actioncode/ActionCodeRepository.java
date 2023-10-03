package com.github.wnameless.spring.boot.up.actioncode;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ActionCodeRepository<AC extends ActionCode<A, T>, A extends Enum<?>, T, ID>
    extends CrudRepository<AC, ID> {

  Optional<AC> findByActionTargetAndAction(T actionTarget, A action);

  Optional<AC> findByActionAndCode(A action, String code);

}
