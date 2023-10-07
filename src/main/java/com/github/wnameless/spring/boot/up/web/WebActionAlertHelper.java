package com.github.wnameless.spring.boot.up.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Alert;
import lombok.Data;

public interface WebActionAlertHelper {

  default void alert(String action) {
    addAlertMessages(SpringBootUp.getBean(ModelAndViewControllerAdvice.class).getModelAndView(),
        action);
  }

  default void alert(String action, ModelAndView mav) {
    addAlertMessages(mav, action);
  }

  @Data
  public static final class AlertMessages {

    private List<String> danger = new ArrayList<>();
    private List<String> warning = new ArrayList<>();
    private List<String> info = new ArrayList<>();
    private List<String> success = new ArrayList<>();

  }

  default void addAlertMessages(ModelAndView mav, String action) {
    String controllerSimpleName = ClassUtils.getUserClass(getClass()).getSimpleName();
    String dangerKey = "controller." + controllerSimpleName + "." + action + ".alert.danger";
    String warningKey = "controller." + controllerSimpleName + "." + action + ".alert.warning";
    String infoKey = "controller." + controllerSimpleName + "." + action + ".alert.info";
    String successKey = "controller." + controllerSimpleName + "." + action + ".alert.success";

    var alert = new AlertMessages();
    SpringBootUp.findMessage(dangerKey).ifPresent(message -> {
      alert.setDanger(Arrays.asList(message.split(",")));
    });
    SpringBootUp.findMessage(warningKey).ifPresent(message -> {
      alert.setWarning(Arrays.asList(message.split(",")));
    });
    SpringBootUp.findMessage(infoKey).ifPresent(message -> {
      alert.setInfo(Arrays.asList(message.split(",")));
    });
    SpringBootUp.findMessage(successKey).ifPresent(message -> {
      alert.setSuccess(Arrays.asList(message.split(",")));
    });
    System.out.println(Alert.name());
    System.out.println(alert);
    mav.addObject(Alert.name(), alert);
  }

}
