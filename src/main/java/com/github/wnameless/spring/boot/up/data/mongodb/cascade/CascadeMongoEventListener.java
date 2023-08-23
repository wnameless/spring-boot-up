package com.github.wnameless.spring.boot.up.data.mongodb.cascade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

public class CascadeMongoEventListener extends AbstractMongoEventListener<Object> {

  private static final String ID = "_id";

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
  public void onAfterConvert(AfterConvertEvent<Object> event) {}

  // event.getSource() -> BSON Document
  @Override
  public void onBeforeDelete(BeforeDeleteEvent<Object> event) {
    // Cascade
    Object docId = event.getSource().get(ID);
    Object source = mongoOperations.findById(docId, event.getType());
    CascadeDeleteCallback callback = new CascadeDeleteCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterDelete(AfterDeleteEvent<Object> event) {}

}
