package se.atg.cmdb.model;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

public class PaginatedCollection<T> {

	public static final String LIMIT_DESC = "Paginate the result list. A limit of 5 will return at most 5 results";
	public static final String START_DESC = "First item index of the result. Used with pagination";

	public Integer limit;
	public Integer start;
	public Collection<T> items;
	public Link next;
	public Link previous;

	public PaginatedCollection(UriBuilder uriBuilder, Optional<Integer> optionalLimit, Optional<Integer> optionalStart, List<T> items) {

		if (optionalStart.isPresent()) {
			start = optionalStart.get();
		}

		if (optionalLimit.isPresent()) {
			limit = optionalLimit.get();

			if (items.size() > limit) {
				final int nextStart = (optionalStart.isPresent()) ? start+limit : limit;
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
}