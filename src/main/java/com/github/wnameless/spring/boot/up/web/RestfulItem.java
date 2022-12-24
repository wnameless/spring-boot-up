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

import org.atteo.evo.inflector.English;
import com.google.common.base.CaseFormat;

public interface RestfulItem<ID> extends JoinablePath {

  default boolean hasBackPathname() {
    return getBackPathname() != null && !getBackPathname().isBlank();
  }

  default String getBackPathname() {
    return null;
  }

  default String getBackPath() {
    return hasBackPathname() ? getBackPathname() : getIndexPath();
  }

  @Override
  default String getRootPath() {
    return getShowPath();
  }

  ID getId();

  default String getBasePath() {
    String lowerHyphen =
        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, this.getClass().getSimpleName());
    String plural = English.plural(lowerHyphen);
    return "/" + plural;
  }

  default String getIndexPath() {
    return getBasePath();
  }

  default String getCreatePath() {
    return getBasePath();
  }

  default String getNewPath() {
    return getBasePath() + "/new";
  }

  default String getEditPath() {
    return getBasePath() + "/" + getId() + "/edit";
  }

  default String getShowPath() {
    return getBasePath() + "/" + getId();
  }

  default String getUpdatePath() {
    return getBasePath() + "/" + getId();
  }

  default String getDeletePath() {
    return getBasePath() + "/" + getId();
  }

  default RestfulItem<ID> withParent(RestfulItem<?> parent) {
    String basePath = parent.getShowPath() + getBasePath();
    ID id = getId();

    return new RestfulItem<ID>() {

      @Override
      public String getBasePath() {
        return basePath;
      }

      @Override
      public ID getId() {
        return id;
      }

    };
  }

  default <CID> RestfulItem<CID> withChild(RestfulItem<CID> child) {
    String basePath = getShowPath() + child.getBasePath();
    CID id = child.getId();

    return new RestfulItem<CID>() {

      @Override
      public String getBasePath() {
        return basePath;
      }

      @Override
      public CID getId() {
        return id;
      }

    };
  }

}
