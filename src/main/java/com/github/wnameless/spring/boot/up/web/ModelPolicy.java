/*
 *
 * Copyright 2020 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up.web;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModelPolicy<I> {

  private boolean enable = true;

  private Supplier<? extends I> defaultItem = new Supplier<I>() {

    @Override
    public I get() {
      return null;
    }

  };

  private Function<? super I, ? extends I> itemInitialized;

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

}
