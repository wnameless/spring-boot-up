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
package com.github.wnameless.spring.boot.up.tagging;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.context.SecurityContextHolder;

public interface Tagged {

  TagList getTagList();

  default List<? extends Tag> getAllTags() {
    List<Tag> tags = new ArrayList<>();
    tags.addAll(getPublicTags());
    tags.addAll(getPersonalTags());
    return tags;
  }

  default List<? extends Tag> getPublicTags() {
    return getTagList().stream().filter(tag -> !tag.isPersonal()).collect(Collectors.toList());
  }

  default List<? extends Tag> getPersonalTags() {
    return getTagList().stream().filter(tag -> tag.isPersonal()).collect(Collectors.toList());
  }

  default List<? extends Tag> getViewableTags() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    List<? extends Tag> userTags = getPersonalTags().stream()
        .filter(pt -> Objects.equals(username, pt.getUsername())).collect(Collectors.toList());
    List<? extends Tag> publicTags = getPublicTags();

    List<Tag> tags = new ArrayList<>();
    tags.addAll(publicTags);
    tags.addAll(userTags);
    return tags;
  }

  default boolean addPublicTag(String label) {
    Tag tag = new Tag();
    tag.setLabel(label);
    return getTagList().add(tag);
  }

  default boolean removePublicTag(String label) {
    Tag tag = new Tag();
    tag.setLabel(label);
    return getTagList().remove(tag);
  }

  default boolean addPersonalTag(String label) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Tag tag = new Tag();
    tag.setLabel(label);
    tag.setUsername(username);
    return getTagList().add(tag);
  }

  default boolean removePersonalTag(String label) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Tag tag = new Tag();
    tag.setLabel(label);
    tag.setUsername(username);
    return getTagList().remove(tag);
  }

}
