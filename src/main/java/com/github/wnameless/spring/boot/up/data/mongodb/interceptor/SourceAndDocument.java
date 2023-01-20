package com.github.wnameless.spring.boot.up.data.mongodb.interceptor;

import org.bson.Document;
import lombok.Data;

@Data
public class SourceAndDocument {

  private final Object source;
  private final Document documnet;

  public SourceAndDocument(Object source, Document documnet) {
    this.source = source;
    this.documnet = documnet;
  }

  public boolean hasSource(Class<?> type) {
    return type.isAssignableFrom(source.getClass());
  }

  @SuppressWarnings("unchecked")
  public <T> T getSource(Class<T> type) {
    return (T) source;
  }

  public Object getSource() {
    return source;
  }

}
