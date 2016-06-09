package se.atg.cmdb.helpers;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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
			return null;
		}
		return list.stream()
			.map(mapper)
			.collect(Collectors.toList());
	}

	public static <T,R> List<R> mapFromTwoList(Document bson, String field1, String field2, BiFunction<? super T, ? super T, ? extends R> mapper) {

		final List<T> list1 = (List<T>) bson.get(field1);
		if (list1 == null) {
			return null;
		}
		final List<T> list2 = (List<T>) bson.get(field2);
		Validate.isTrue(list1.size() == list2.size());

		final List<R> result = new ArrayList<>(list1.size());
		for (int i=0; i < list1.size(); ++i) {

			final R mapped = mapper.apply(list1.get(i), list2.get(i));
			result.add(mapped);
		}
		return result;
	}

	public static Map<String, Object> mapAttributes(Document bson) {

		final Map<String, Object> map = bson.get("attributes", Map.class);
		if (map == null) {
			return null;
		}
		return Maps.newLinkedHashMap(map);
	}

	public static <T,R> R mapObject(Document bson, String field, Function<? super T, ? extends R> mapper, Class<T> type) {

		final T object = (T) bson.get(field);
		if (object == null) {
			return null;
		}
		return mapper.apply(object);
	}

	public static <T,R> R mapObject(Document bson, String field, Function<? super T, ? extends R> mapper) {

		final T object = (T) bson.get(field);
		if (object == null) {
			return null;
		}
		return mapper.apply(object);
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

	public static Document getMeta(Document bson) {
		return bson.get("meta", Document.class);
	}

	public static Optional<String> getHash(Document bson) {
		final Document meta = getMeta(bson);
		return Optional.ofNullable(meta).map(t->t.getString("hash"));
	}
}