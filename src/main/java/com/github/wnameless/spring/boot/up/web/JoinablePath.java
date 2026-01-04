package com.github.wnameless.spring.boot.up.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface JoinablePath {

  public static JoinablePath of(String rootPath) {
    return new JoinablePath() {

      @Override
      public String getRootPath() {
        return rootPath;
      }

    };
  }

  String getRootPath();

  default String joinPath(String... paths) {
    String pathSeprator = "/";

    List<String> list = new ArrayList<>();
    list.add(getRootPath());
    for (String path : paths) {
      if (path != null && !path.isEmpty()) {
        list.add(path);
      }
    }
    for (int i = 1; i < list.size(); i++) {
      int predecessor = i - 1;
      while (list.get(predecessor).endsWith(pathSeprator)) {
        list.set(predecessor,
            list.get(predecessor).substring(0, list.get(predecessor).length() - 1));
      }
      while (list.get(i).startsWith(pathSeprator)) {
        list.set(i, list.get(i).substring(1));
      }
      list.set(i, pathSeprator + list.get(i));
    }

    StringBuilder sb = new StringBuilder();
    list.stream().forEach(path -> sb.append(path));
    return sb.toString();
  }

}
