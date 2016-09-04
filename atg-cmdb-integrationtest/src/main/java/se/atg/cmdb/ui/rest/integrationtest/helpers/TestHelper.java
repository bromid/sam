package se.atg.cmdb.ui.rest.integrationtest.helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.bson.Document;
import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.atg.cmdb.helpers.JsonHelper;
import se.atg.cmdb.model.Base;

public abstract class TestHelper {

  public static <T> Document addMetaForCreate(T node, ObjectMapper objectMapper) {
    return JsonHelper.addMetaForCreate(node, "integration-test", objectMapper);
  }

  public static <T> List<Document> addMetaForCreate(Collection<T> nodes, ObjectMapper objectMapper) {
    return nodes.stream()
      .map(t -> addMetaForCreate(t, objectMapper))
      .collect(Collectors.toList());
  }

  public static <K,V> void assertEquals(Map<K,V> expected, Collection<V> actual, Function<? super V,K> keyMapper) {
    assertEquals(expected, actual, keyMapper, Assert::assertEquals);
  }

  public static <K,V> void assertEquals(Collection<V> expected, Collection<V> actual, Function<? super V,K> keyMapper) {
    assertEquals(expected, actual, keyMapper, Assert::assertEquals);
  }

  public static <K,V> void assertEquals(
      Collection<V> expected,
      Collection<V> actual,
      Function<? super V,K> keyMapper,
      BiConsumer<V,V> assertion
    ) {

    final Map<K, V> expectedMap = expected.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
    assertEquals(expectedMap, actual, keyMapper, assertion);
  }

  public static <K,V,T> void assertEquals(
      Map<K,V> expectedMap,
      Collection<T> actualCollection,
      Function<? super T,K> keyMapper,
      BiConsumer<V,T> assertion
    ) {

    Assert.assertEquals(expectedMap.size(), actualCollection.size());
    actualCollection.forEach(actual -> {
      final K key = keyMapper.apply(actual);
      final V expected = expectedMap.get(key);
      Assert.assertNotNull("Missing " + key, expected);
      assertion.accept(expected, actual);
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

  public static void assertValidationError(String expected, Response response) {
    Assert.assertEquals(422, response.getStatus());
    final String jsonResponse = response.readEntity(String.class);
    Assert.assertTrue(jsonResponse + " doesn't contain \"" + expected + "\".", jsonResponse.contains(expected));
  }

  public static void isEqualExceptMeta(Base expected, Base actual) {
    Assert.assertNotNull("Expected " + actual + " to have meta", actual.meta);
    isEqualDisregardMeta(expected, actual);
  }

  public static void isEqualDisregardMeta(Base expected, Base actual) {
    actual.meta = null;
    Assert.assertEquals("Got " + actual + " expected " + expected, expected, actual);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Object mapPath(Map<String, Object> map, String... path) {

    Map<String, Object> curr = map;
    for (String field : Arrays.copyOfRange(path, 0, path.length - 1)) {
      curr = (Map) curr.get(field);
    }
    return curr.get(path[path.length - 1]);
    
  }
}
