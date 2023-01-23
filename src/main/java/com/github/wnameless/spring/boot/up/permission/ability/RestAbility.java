package com.github.wnameless.spring.boot.up.permission.ability;

public enum RestAbility implements Ability {

  MANAGE, CRUD, CREATE, READ, UPDATE, DELETE;

  @Override
  public String getAbilityName() {
    return name();
  }

}
