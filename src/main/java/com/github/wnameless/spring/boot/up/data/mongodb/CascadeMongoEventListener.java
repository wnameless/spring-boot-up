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
package com.github.wnameless.spring.boot.up.data.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

import net.sf.rubycollect4j.Ruby;
import net.sf.rubycollect4j.RubyHash;

public class CascadeMongoEventListener
    extends AbstractMongoEventListener<Object> {

  private static final int CACHE_SIZE = 256;

  private final RubyHash<Object, CascadeDeleteCallback> cascadeDeleteCallbacks =
      Ruby.Hash.create();

  @Autowired
  private MongoOperations mongoOperations;

  @Override
  public synchronized void onBeforeConvert(BeforeConvertEvent<Object> event) {
    Object source = event.getSource();
    CascadeSaveUpdateCallback callback =
        new CascadeSaveUpdateCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);
  }

  @Override
  public synchronized void onAfterSave(AfterSaveEvent<Object> event) {
    Object source = event.getSource();
    ParentRefCallback callback = new ParentRefCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);
  }

  @Override
  public synchronized void onAfterConvert(AfterConvertEvent<Object> event) {
    Object source = event.getSource();
    CascadeDeleteCallback callback =
        new CascadeDeleteCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);
    Object docId = event.getDocument().get("_id");
    if (docId != null && !callback.getDeletableIds().isEmpty()) {
      cascadeDeleteCallbacks.put(docId, callback);
      if (cascadeDeleteCallbacks.size() > CACHE_SIZE) {
        while (cascadeDeleteCallbacks.size() > CACHE_SIZE) {
          cascadeDeleteCallbacks.shift();
        }
      }
    }
  }

  @Override
  public synchronized void onAfterDelete(AfterDeleteEvent<Object> event) {
    Object docId = event.getSource().get("_id");
    if (cascadeDeleteCallbacks.containsKey(docId)) {
      CascadeDeleteCallback callback = cascadeDeleteCallbacks.remove(docId);
      for (DeletableId deletableId : callback.getDeletableIds()) {
        Query searchQuery = new Query(
            Criteria.where(callback.getIdFieldName()).is(deletableId.getId()));
        mongoOperations.remove(searchQuery, deletableId.getType());
      }
    }
  }

}