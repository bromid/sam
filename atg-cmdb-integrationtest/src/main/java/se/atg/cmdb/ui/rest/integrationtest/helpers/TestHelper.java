package se.atg.cmdb.ui.rest.integrationtest.helpers;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.junit.Assert;

public abstract class TestHelper {

	public static <K,V> void assertEquals(Map<K, V> expectedMap, Collection<V> actual, Function<? super V,K> keyMapper) {
		assertEquals(expectedMap, actual, keyMapper, Assert::assertEquals);
	}

	public static <K,V> void assertEquals(Collection<V> expected, Collection<V> actual, Function<? super V,K> keyMapper) {
		assertEquals(expected, actual, keyMapper, Assert::assertEquals);
	}

	public static <K,V,T> void assertEquals(Collection<V> expected, Collection<V> actual, Function<? super V,K> keyMapper, BiConsumer<V,T> assertion) {

		final Map<K, V> expectedMap = expected.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
		assertEquals(expectedMap, actual, keyMapper);
	}

	public static <K,V,T> void assertEquals(Map<K, V> expectedMap, Collection<T> actual, Function<? super T,K> keyMapper, BiConsumer<V,T> assertion) {

		Assert.assertEquals(expectedMap.size(), actual.size());
		actual.forEach(t->{
			final K key = keyMapper.apply(t);
			final V expected = expectedMap.get(key);
			Assert.assertNotNull("Missing " + key, expected);
			assertion.accept(expected, t);
		});
	}

	public static void assertSuccessful(Response response) {

		final StatusType responseStatus = response.getStatusInfo();
		if (responseStatus.getFamily() != Status.Family.SUCCESSFUL) {

			final StringBuilder sb = new StringBuilder();
	        sb.append("Response ").append(responseStatus.getStatusCode()).append(" ").append(responseStatus.getReasonPhrase());
	        if (response.hasEntity()) {
	            sb.append(", Response: ").append(response.readEntity(String.class));
	        }
	        throw new AssertionError(sb);
		}
	}
}
