package se.atg.cmdb.helpers;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.bson.Document;

import com.google.common.collect.Maps;

@SuppressWarnings("unchecked")
public abstract class Mapper {

  public static <T,R> List<R> mapList(Document bson, String field, Function<? super T, ? extends R> mapper) {

    final List<T> list = (List<T>) bson.get(field);
    if (list == null) {
      return Collections.emptyList();
    }
    return list.stream()
      .map(mapper)
      .collect(Collectors.toList());
  }

  public static <T,R> List<R> mapList(Document bson, String field, Map<T, Document> map, Function<Document,T> keyMapper, BiFunction<Document,Document,R> mapper) {

    final List<Document> list = (List<Document>) bson.get(field);
    if (list == null) {
      return Collections.emptyList();
    }
    return list.stream()
      .map(t -> {
        final T key = keyMapper.apply(t);
        final Document mapItem = map.get(key);
        return mapper.apply(t, mapItem);
      })
      .collect(Collectors.toList());
  }

  public static <T> Map<T, Document> mapListToMap(Document bson, String field, Function<Document,T> keyMapper) {

    final List<Document> list = (List<Document>) bson.get(field);
    if (list == null) {
      return Collections.emptyMap();
    }
    return list.stream()
      .collect(Collectors.toMap(keyMapper, Function.identity()));
  }

  public static <T,R> List<R> mapFromTwoList(Document bson, String field1, String field2, BiFunction<? super T, ? super T, ? extends R> mapper) {

    final List<T> list1 = (List<T>) bson.get(field1);
    if (list1 == null) {
      return Collections.emptyList();
    }
    final List<T> list2 = (List<T>) bson.get(field2);
    Validate.isTrue(list1.size() == list2.size());

    final List<R> result = new ArrayList<>(list1.size());
    for (int i = 0; i < list1.size(); ++i) {

      final R mapped = mapper.apply(list1.get(i), list2.get(i));
      result.add(mapped);
    }
    return result;
  }

  public static <T,R> R mapObject(Document bson, String field, Function<? super T, ? extends R> mapper) {

    final Object object = bson.get(field);
    if (object == null) {
      return null;
    }
    if (object instanceof List) {
      final List<T> list = (List<T>) object;
      if (list.size() == 0) {
        return null;
      }
      Validate.isTrue(list.size() == 1);
      return mapper.apply(list.get(0));
    }
    return mapper.apply((T) object);
  }

  public static void upsertList(Document bson, Document update, String field, BiFunction<Document,Document,Boolean> equals) {

    final List<Document> existingList = (List<Document>) bson.get(field);
    if (existingList == null) {
      bson.put(field, Arrays.asList(update));
    } else {
      final Iterator<Document> iterator = existingList.iterator();
      while (iterator.hasNext()) {
        if (equals.apply(iterator.next(), update)) {
          iterator.remove();
          break;
        }
      }
      existingList.add(update);
    }
  }

  public static ZonedDateTime mapDateTime(Document bson, String field) {

    final Date date = bson.get(field, Date.class);
    if (date == null) {
      return null;
    }
    return ZonedDateTime
      .ofInstant(date.toInstant(), ZoneOffset.UTC)
      .withZoneSameInstant(ZoneId.systemDefault());
  }

  public static Map<String,Object> mapAttributes(Document bson) {

    final Map<String, Object> map = bson.get("attributes", Map.class);
    if (map == null) {
      return Collections.emptyMap();
    }
    return Maps.newLinkedHashMap(map);
  }

  public static Document getMeta(Document bson) {
    return bson.get("meta", Document.class);
  }

  public static Optional<String> getHash(Document bson) {
    final Document meta = getMeta(bson);
    return Optional.ofNullable(meta).map(t -> t.getString("hash"));
  }
}
