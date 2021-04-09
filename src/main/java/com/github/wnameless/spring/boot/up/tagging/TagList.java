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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class TagList implements Set<Tag> {

  private Set<Tag> tags = new TreeSet<>();

  @Override
  public int size() {
    return tags.size();
  }

  @Override
  public boolean isEmpty() {
    return tags.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return tags.contains(o);
  }

  @Override
  public Iterator<Tag> iterator() {
    return tags.iterator();
  }

  @Override
  public Object[] toArray() {
    return tags.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return tags.toArray(a);
  }

  @Override
  public boolean add(Tag e) {
    return tags.add(e);
  }

  @Override
  public boolean remove(Object o) {
    return tags.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return tags.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends Tag> c) {
    return tags.addAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return tags.retainAll(c);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return tags.removeAll(c);
  }

  @Override
  public void clear() {
    tags.clear();
  }

  @Override
  public String toString() {
    return tags.toString();
  }

}
