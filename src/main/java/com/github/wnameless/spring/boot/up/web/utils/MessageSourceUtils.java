package com.github.wnameless.spring.boot.up.web.utils;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import com.github.wnameless.spring.boot.up.web.JoinablePath;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageSourceUtils {

  @SuppressWarnings("null")
  public MessageSource createByClasspathFolder(String classpathFolder, String... basenames)
      throws IOException {
    ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();

    for (var basename : basenames) {
      messageSource.setBasename("classpath:" + basename);
    }

    var classpathFolderPath = JoinablePath.of(classpathFolder).joinPath("/");

    var propResources = new PathMatchingResourcePatternResolver()
        .getResources("classpath:" + classpathFolderPath + "*.properties");
    var propBasenames = List.of(propResources).stream()
        .map(r -> "classpath:" + classpathFolderPath
            + r.getFilename().replaceAll(Pattern.quote(".properties") + "$", ""))
        .toArray(String[]::new);
    messageSource.addBasenames(propBasenames);

    var xmlResources = new PathMatchingResourcePatternResolver()
        .getResources("classpath:" + classpathFolderPath + "*.xml");
    var xmlBasenames =
        List.of(xmlResources).stream()
            .map(r -> "classpath:" + classpathFolderPath
                + r.getFilename().replaceAll(Pattern.quote(".xml") + "$", ""))
            .toArray(String[]::new);
    messageSource.addBasenames(xmlBasenames);

    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

}
