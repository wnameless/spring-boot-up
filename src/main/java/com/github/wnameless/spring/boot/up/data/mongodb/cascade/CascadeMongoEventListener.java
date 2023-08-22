package com.github.wnameless.spring.boot.up.data.mongodb.cascade;

import org.springframework.beans.factory.InitializingBean;
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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class CascadeMongoEventListener extends AbstractMongoEventListener<Object>
    implements InitializingBean {

  private static final String ID = "_id";

  @Value("${spring.boot.up.data.mongodb.cacade.delete.cache.size:2020}")
  // Default batch size of MongoDB is 101
  private int CASCADE_DELETE_CALLBACK_CACHE_SIZE = 2020;

  // Cache<ID,CascadeDeleteCallback>
  private Cache<Object, CascadeDeleteCallback> cascadeDeleteCallbackCache;

  @Override
  public void afterPropertiesSet() throws Exception {
    cascadeDeleteCallbackCache =
        Caffeine.newBuilder().maximumSize(CASCADE_DELETE_CALLBACK_CACHE_SIZE).build();
  }

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
    CascadeDeleteCallback callback = new CascadeDeleteCallback(source);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Cache deletable callback
    Object docId = event.getDocument().get(ID);
    if (docId != null && !callback.getDeletableIds().isEmpty()) {
      cascadeDeleteCallbackCache.put(docId, callback);
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
    if (cascadeDeleteCallbackCache.asMap().containsKey(docId)) {
      CascadeDeleteCallback callback = cascadeDeleteCallbackCache.asMap().remove(docId);
      for (DeletableId deletableId : callback.getDeletableIds()) {
        Query searchQuery = new Query(Criteria.where(ID).is(deletableId.getId()));
        mongoOperations.remove(searchQuery, deletableId.getType());
      }
    }
  }

}
