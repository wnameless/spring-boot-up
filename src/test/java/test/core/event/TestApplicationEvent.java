package test.core.event;

import org.springframework.context.ApplicationEvent;

public class TestApplicationEvent extends ApplicationEvent {

  public TestApplicationEvent(String source) {
    super(source);
  }

}
