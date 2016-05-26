package se.atg.cmdb.helpers;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	public static Map<String, Object> mapAttributes(Document bson) {

		final Map<String, Object> map = bson.get("attributes", Map.class);
		if (map == null) {
			return null;
		}
		return Maps.newLinkedHashMap(map);
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

	public static String getHash(Document bson) {
		final Document meta = getMeta(bson);
		return (meta == null) ? null : meta.getString("hash");
	}
}