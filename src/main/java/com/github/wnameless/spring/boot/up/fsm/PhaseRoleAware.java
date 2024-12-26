package com.github.wnameless.spring.boot.up.fsm;

import java.util.List;
import com.github.wnameless.spring.boot.up.permission.role.Role;

public interface PhaseRoleAware {

  List<Role> getRoles();

}
