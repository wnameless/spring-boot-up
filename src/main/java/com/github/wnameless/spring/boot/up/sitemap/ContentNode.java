package com.github.wnameless.spring.boot.up.sitemap;

import static lombok.AccessLevel.PRIVATE;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class ContentNode {

  final String path;
  final String name;

}
