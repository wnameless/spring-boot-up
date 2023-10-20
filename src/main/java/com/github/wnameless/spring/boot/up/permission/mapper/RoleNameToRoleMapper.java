package com.github.wnameless.spring.boot.up.permission.mapper;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import com.github.wnameless.spring.boot.up.permission.role.Role;

@Mapper(componentModel = "spring")
public interface RoleNameToRoleMapper extends Converter<String, Role> {

  @Override
  default Role convert(String roleName) {
    return Role.of(roleName);
  }

}
