package com.github.wnameless.spring.boot.up.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Alert;
import lombok.Data;

public interface WebActionAlertHelper {

  default void alert(String action, Object... args) {
    addAlertMessages(SpringBootUp.getBean(SpringBootUpControllerAdvice.class).getModelAndView(),
        action, args);
  }

  default void alert(String action, ModelAndView mav, Object... args) {
    addAlertMessages(mav, action, args);
  }

  @Data
  public static final class AlertMessages {

    private List<String> danger = new ArrayList<>();
    private List<String> warning = new ArrayList<>();
    private List<String> info = new ArrayList<>();
    private List<String> success = new ArrayList<>();

  }

  default void addAlertMessages(ModelAndView mav, String action, Object... args) {
    String controllerSimpleName = ClassUtils.getUserClass(getClass()).getSimpleName();
    String dangerKey = "controller." + controllerSimpleName + "." + action + ".alert.danger";
    String warningKey = "controller." + controllerSimpleName + "." + action + ".alert.warning";
    String infoKey = "controller." + controllerSimpleName + "." + action + ".alert.info";
    String successKey = "controller." + controllerSimpleName + "." + action + ".alert.success";

    var alert = Optional.ofNullable((AlertMessages) mav.getModel().get(Alert.name()))
        .orElse(new AlertMessages());
    SpringBootUp.findMessage(dangerKey, args).ifPresent(message -> {
      alert.getDanger().addAll(Arrays.asList(message.split(",")));
    });
    SpringBootUp.findMessage(warningKey, args).ifPresent(message -> {
      alert.getWarning().addAll(Arrays.asList(message.split(",")));
    });
    SpringBootUp.findMessage(infoKey, args).ifPresent(message -> {
      alert.getInfo().addAll(Arrays.asList(message.split(",")));
    });
    SpringBootUp.findMessage(successKey, args).ifPresent(message -> {
      alert.getSuccess().addAll(Arrays.asList(message.split(",")));
    });
    mav.addObject(Alert.name(), alert);
  }

}
