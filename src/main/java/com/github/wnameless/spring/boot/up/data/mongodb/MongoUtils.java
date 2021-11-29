/*
 *
 * Copyright 2021 Wei-Ming Wu
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

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.base.JacksonJsonValue;
import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.querydsl.core.types.Path;

public final class MongoUtils {

  private static final Logger log = LoggerFactory.getLogger(MongoUtils.class);

  private MongoUtils() {}

  public static String[] findDotPaths(Class<?> klass) {
    try {
      Object obj = klass.getDeclaredConstructor().newInstance();
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.valueToTree(obj);

      JsonFlattener jf = new JsonFlattener(new JacksonJsonValue(jsonNode));
      Map<String, Object> flattenedMap =
          jf.withFlattenMode(FlattenMode.KEEP_ARRAYS).flattenAsMap();

      log.debug(flattenedMap.keySet().toString());
      return flattenedMap.keySet().stream().toArray(String[]::new);
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  public static String[] findDotPaths(Path<?>[] paths) {
    Set<String> dotPaths = new LinkedHashSet<>();

    for (Path<?> path : paths) {
      String[] pathStrs = path.toString().split(Pattern.quote("."));
      if (pathStrs.length > 1) {
        String dotPath = IntStream.range(1, pathStrs.length)
            .mapToObj(idx -> pathStrs[idx]).collect(Collectors.joining("."));
        dotPaths.add(dotPath);
      }
    }

    return dotPaths.stream().toArray(String[]::new);
  }

}
