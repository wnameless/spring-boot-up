package com.github.wnameless.spring.boot.up.permission.ability;

public interface Ability {

  public static Ability of(String abilityName) {
    return new SimpleAbility(abilityName);
  }

  String getAbilityName();

  default String getUpperCaseAbilityName() {
    return getAbilityName().toUpperCase();
  }

}
