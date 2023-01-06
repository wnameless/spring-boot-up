package com.github.wnameless.spring.boot.up.jsf;

import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;

public interface JsfDataInitilizer<JD extends JsfData<JS, ID>, JS extends JsfSchema<ID>, ID>
    extends JsfVersioning {

  default JD initJsfData() {
    return (JD) initJsfData(getFormType(), getFormBranch());
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  default JD initJsfData(String formType, String formBranch) {
    JsfService jsfService = SpringBootUp.getBean(JsfService.class);

    return (JD) jsfService.newJsfData(formType, formBranch);
  }

}
