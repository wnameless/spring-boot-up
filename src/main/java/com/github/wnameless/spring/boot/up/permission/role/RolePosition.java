package com.github.wnameless.spring.boot.up.permission.role;

import java.util.Optional;

public interface RolePosition extends Rolify {

  Optional<Rolify> getManager();

}
