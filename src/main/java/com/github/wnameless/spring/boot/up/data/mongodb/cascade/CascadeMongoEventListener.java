package com.github.wnameless.spring.boot.up.data.mongodb.cascade;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

public class CascadeMongoEventListener extends AbstractMongoEventListener<Object> {

  private static final String ID = "_id";

  @Value("${spring.boot.up.data.mongodb.cacade.delete.cache.size:256}")
  private int CASCADE_DELETE_CALLBACK_CACHE_SIZE = 256;

  private final Map<Object, CascadeDeleteCallback> cascadeDeleteCallbacks =
      Collections.synchronizedMap(

          new LinkedHashMap<Object, CascadeDeleteCallback>() {

            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, CascadeDeleteCallback> entry) {
              return size() > CASCADE_DELETE_CALLBACK_CACHE_SIZE;
            }

          });

  @Autowired
  private MongoOperations mongoOperations;

  // event.getSource() -> Java Object
  @Override
  public void onBeforeConvert(BeforeConvertEvent<Object> event) {
    // Cascade
    Object source = event.getSource();
    CascadeSaveUpdateCallback callback = new CascadeSaveUpdateCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);
  }

  // event.getSource() -> Java Object
  @Override
  public void onBeforeSave(BeforeSaveEvent<Object> event) {}

  // event.getSource() -> Java Object
  @Override
  public void onAfterSave(AfterSaveEvent<Object> event) {
    // Cascade
    Object source = event.getSource();
    ParentRefCallback callback = new ParentRefCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterLoad(AfterLoadEvent<Object> event) {}

  // event.getSource() -> Java Object
  @Override
  public void onAfterConvert(AfterConvertEvent<Object> event) {
    // Cascade
    Object source = event.getSource();
    CascadeDeleteCallback callback = new CascadeDeleteCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Cache deletable callback
    Object docId = event.getDocument().get(ID);
    if (docId != null && !callback.getDeletableIds().isEmpty()) {
      cascadeDeleteCallbacks.put(docId, callback);
    }
  }

  // event.getSource() -> BSON Document
  @Override
  public void onBeforeDelete(BeforeDeleteEvent<Object> event) {}

  // event.getSource() -> BSON Document
  @Override
  public void onAfterDelete(AfterDeleteEvent<Object> event) {
    // Cascade
    Object docId = event.getSource().get(ID);
    if (cascadeDeleteCallbacks.containsKey(docId)) {
      CascadeDeleteCallback callback = cascadeDeleteCallbacks.remove(docId);
      for (DeletableId deletableId : callback.getDeletableIds()) {
        Query searchQuery = new Query(Criteria.where(ID).is(deletableId.getId()));
        mongoOperations.remove(searchQuery, deletableId.getType());
      }
    }
  }

}
