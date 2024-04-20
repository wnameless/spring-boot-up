package com.github.wnameless.spring.boot.up.web.utils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.querydsl.core.types.Predicate;
import lombok.experimental.UtilityClass;
import net.sf.rubycollect4j.Ruby;

@UtilityClass
public class PageableUtils {

  public <I> Page<Entry<String, Collection<I>>> groupByPage(Pageable pageable, Predicate predicate,
      Order groupOn, Function<I, String> groupByStrategy,
      BiFunction<Predicate, Sort, Iterable<I>> itemStrategy) {
    var pageSize = pageable.getPageSize();
    var pageNumber = pageable.getPageNumber();
    var sort = pageable.getSort();

    var thisGroupOn = sort.stream().filter(o -> o.getProperty().equals(groupOn.getProperty()))
        .findFirst().orElse(groupOn);
    var thisSort = Sort.by(sort.stream().filter(o -> !o.getProperty().equals(groupOn.getProperty()))
        .toArray(Order[]::new));
    thisSort = Sort.by(thisSort.and(thisGroupOn).stream().toArray(Order[]::new));

    var items = Ruby.LazyEnumerator.of(itemStrategy.apply(predicate, thisSort));
    int groupByCount = StreamSupport.stream(items.spliterator(), false).map(groupByStrategy)
        .collect(Collectors.toSet()).size();

    int skip = pageNumber * pageSize;
    var groupNames = new LinkedHashSet<String>();
    do {
      if (items.hasNext()) {
        groupNames.add(groupByStrategy.apply(items.peek()));
      }
      if (items.hasNext() && groupNames.size() <= skip) {
        items.next();
      }
    } while (items.hasNext() && groupNames.size() <= skip);

    Multimap<String, I> content = ArrayListMultimap.create();
    while (items.hasNext() && content.keySet().size()
        + (content.keySet().contains(groupByStrategy.apply(items.peek())) ? 0 : 1) <= pageSize) {
      var item = items.next();
      content.put(groupByStrategy.apply(item), item);
    }

    return new PageImpl<>(content.asMap().entrySet().stream().toList(),
        PageRequest.of(pageNumber, pageSize, thisSort), groupByCount);
  }

}
