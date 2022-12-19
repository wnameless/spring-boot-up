package com.github.wnameless.spring.boot.up.data.mongodb;

import org.bson.Document;
import org.springframework.data.mongodb.core.mapping.event.MongoMappingEvent;
import lombok.Data;


@Data
public class SourceAndDocument {

  public enum SourceType {
    JAVA_OBJECT, BSON_DOCUMENT;
  }

  private final SourceType sourceType;
  private final Object source;
  private final Document documnet;

  public SourceAndDocument(MongoMappingEvent<?> mongoMappingEvent) {
    this.source = mongoMappingEvent.getSource();
    this.documnet = mongoMappingEvent.getDocument();
    if (this.source instanceof Document) {
      this.sourceType = SourceType.BSON_DOCUMENT;
    } else {
      this.sourceType = SourceType.JAVA_OBJECT;
    }
  }

  public boolean hasSource(Class<?> type) {
    return type.getClass().isAssignableFrom(source.getClass());
  }

  @SuppressWarnings("unchecked")
  public <T> T getSource(Class<T> type) {
    return (T) source;
  }

  public Object getSource() {
    return source;
  }

  public boolean isSourceJavaObject() {
    return sourceType == SourceType.JAVA_OBJECT;
  }

  public boolean isSourceBsonDocument() {
    return sourceType == SourceType.BSON_DOCUMENT;
  }

}
