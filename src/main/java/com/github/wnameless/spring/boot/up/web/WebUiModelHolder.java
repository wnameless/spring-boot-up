package com.github.wnameless.spring.boot.up.web;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class WebUiModelHolder {

  private final Cache<HttpServletRequest, Model> modelCache = Caffeine.newBuilder().weakKeys().weakValues()
      .expireAfterWrite(Duration.ofMinutes(1)).build();

  public void cacheModel(HttpServletRequest req, Model model) {
    modelCache.put(req, model);
  }

  public Model retrieveModel(HttpServletRequest req) {
    return modelCache.getIfPresent(req);
  }

}
