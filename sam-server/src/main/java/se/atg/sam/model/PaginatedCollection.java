package se.atg.sam.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import se.atg.sam.ui.rest.Defaults;

public class PaginatedCollection<T> {

  public static final String LIMIT_DESC = "Paginate the result list. A limit of 5 will return at most 5 results";
  public static final String START_DESC = "First item index of the result. Used with pagination";

  public Integer limit;
  public Integer start;
  public Integer total;
  public Collection<T> items;
  public Link next;
  public Link previous;

  @JsonCreator
  public PaginatedCollection(
    @JsonProperty("limit") Integer limit,
    @JsonProperty("start") Integer start,
    @JsonProperty("next") Link next,
    @JsonProperty("previous") Link previous,
    @JsonProperty("items") Collection<T> items
  ) {
    this.limit = limit;
    this.start = start;
    this.next = next;
    this.previous = previous;
    this.items = items;
  }

  public PaginatedCollection(UriBuilder uriBuilder, Optional<Integer> optionalLimit, Optional<Integer> optionalStart, List<T> items) {

    if (optionalStart.isPresent()) {
      start = optionalStart.get();
    }

    if (optionalLimit.isPresent()) {
      limit = optionalLimit.get();

      if (items.size() > limit) {
        final int nextStart = (optionalStart.isPresent()) ? start + limit : limit;
        this.items = items.subList(0, limit);
        this.next = Link.fromUriBuilder(uriBuilder.replaceQueryParam("limit", limit).replaceQueryParam("start", nextStart)).rel("next").build();
      }
      if (optionalStart.isPresent()) {

        final int nextStart = limit - start;
        final UriBuilder prevBuilder;
        if (nextStart > 0) {
          prevBuilder = uriBuilder.replaceQueryParam("limit", limit).replaceQueryParam("start", nextStart);
        } else {
          prevBuilder = uriBuilder.replaceQueryParam("limit", limit).replaceQueryParam("start", (Object[]) null);
        }
        this.previous = Link.fromUriBuilder(prevBuilder).rel("prev").build();
      }
    }
    this.items = (this.items == null) ? items : this.items;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, Defaults.STYLE);
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
