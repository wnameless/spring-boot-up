package com.github.wnameless.spring.boot.up.data.mongodb.converter;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

public final class MongoConverter {

  private MongoConverter() {}

  public static MongoCustomConversions timeConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new YearWriteConverter());
    converters.add(new YearReadConverter());
    converters.add(new YearMonthWriteConverter());
    converters.add(new YearMonthReadConverter());
    converters.add(new LocalTimeWriteConverter());
    converters.add(new LocalTimeReadConverter());
    converters.add(new LocalDateWriteConverter());
    converters.add(new LocalDateReadConverter());
    converters.add(new LocalDateTimeWriteConverter());
    converters.add(new LocalDateTimeReadConverter());
    converters.add(new ZonedDateTimeWriteConverter());
    converters.add(new ZonedDateTimeReadConverter());
    return new MongoCustomConversions(converters);
  }

}
