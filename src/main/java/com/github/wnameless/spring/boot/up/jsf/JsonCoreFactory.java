package com.github.wnameless.spring.boot.up.jsf;

import com.github.wnameless.json.base.JacksonJsonCore;
import com.github.wnameless.json.base.JsonCore;
import com.github.wnameless.json.base.JsonValueBase;

public enum JsonCoreFactory {

  INSTANCE;

  private JsonCore<?> jsonCore = new JacksonJsonCore();

  public void setJsonCore(JsonCore<?> jsonCore) {
    this.jsonCore = jsonCore;
  }

  public JsonCore<?> getJsonCore() {
    return jsonCore;
  }

  public JsonValueBase<?> readJson(String json) {
    return jsonCore.parse(json);
  }

}
