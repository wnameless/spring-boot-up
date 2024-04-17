package com.github.wnameless.spring.boot.up.fsm;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StateAuditTrail<S extends State<T, ID>, T extends Trigger, ID> {

  private T trigger;

  private S state;

  private String username;

  private LocalDateTime timestamp;

  public StateAuditTrail() {}

  public StateAuditTrail(T trigger, S state, String username) {
    this.trigger = trigger;
    this.state = state;
    this.username = username;
    timestamp = LocalDateTime.now();
  }

}
