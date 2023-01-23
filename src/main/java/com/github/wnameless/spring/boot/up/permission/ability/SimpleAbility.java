package com.github.wnameless.spring.boot.up.permission.ability;

import lombok.Data;
import lombok.NonNull;

@Data
public class SimpleAbility implements Ability {

  private final String abilityName;

  public SimpleAbility(@NonNull String abilityName) {
    this.abilityName = abilityName.toUpperCase();
  }

}
