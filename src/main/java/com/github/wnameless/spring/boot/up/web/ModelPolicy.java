package com.github.wnameless.spring.boot.up.web;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModelPolicy<I> {

  private boolean enable = true;

  private Supplier<? extends I> defaultItem = new Supplier<I>() {

    @Override
    public I get() {
      return null;
    }

  };

  private Supplier<QuerySetting<?>> querySetting = null;

  private Function<QueryConfig<?>, QueryConfig<?>> queryConfig = null;

  private Function<? super I, ? extends I> itemInitialized;

  private UnaryOperator<Object> parentInitialized;

  public ModelPolicy() {}

  public ModelPolicy(I defaultItem) {
    this.defaultItem = new Supplier<I>() {

      @Override
      public I get() {
        return defaultItem;
      }

    };
  }

  public ModelPolicy<I> enable() {
    enable = true;
    return this;
  }

  public ModelPolicy<I> disable() {
    enable = false;
    return this;
  }

  public boolean isEnable() {
    return enable;
  }

  public boolean isDisable() {
    return !enable;
  }

  public ModelPolicy<I> forDefaultItem(Supplier<? extends I> defaultItem) {
    this.defaultItem = defaultItem;
    return this;
  }

  public Supplier<? extends I> onDefaultItem() {
    return defaultItem;
  }

  public ModelPolicy<I> forItemInitialized(Function<I, I> itemInitialized) {
    this.itemInitialized = itemInitialized;
    return this;
  }

  public Function<? super I, ? extends I> onItemInitialized() {
    return itemInitialized;
  }

  public ModelPolicy<I> forParentInitialized(UnaryOperator<Object> parentInitialized) {
    this.parentInitialized = parentInitialized;
    return this;
  }

  public UnaryOperator<Object> onParentInitialized() {
    return parentInitialized;
  }

  public ModelPolicy<I> forQuerySetting(Supplier<QuerySetting<?>> querySetting) {
    this.querySetting = querySetting;
    return this;
  }

  public Supplier<QuerySetting<?>> onQuerySetting() {
    return querySetting;
  }

  public ModelPolicy<I> forQueryConfig(Function<QueryConfig<?>, QueryConfig<?>> queryConfig) {
    this.queryConfig = queryConfig;
    return this;
  }

  public Function<QueryConfig<?>, QueryConfig<?>> onQueryConfig() {
    return queryConfig;
  }

}
