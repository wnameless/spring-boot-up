package com.github.wnameless.spring.boot.up.fsm;

import java.time.Instant;
import lombok.Data;

@Data
public class StateAuditTrail<S extends State<T, ID>, T extends Trigger, ID> {

  private T trigger;

  private S state;

  private S originState;

  private String username;

  private Instant timestamp;

  public StateAuditTrail() {}

  public StateAuditTrail(S originState, T trigger, S state, String username) {
    this.originState = originState;
    this.trigger = trigger;
    this.state = state;
    this.username = username;
    timestamp = Instant.now();
  }

}
