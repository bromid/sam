package se.atg.cmdb.model;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.Link;

public class PaginatedCollection<T> {

	public Collection<T> items;
	public Integer pageSize;
	public Link next;
	public Link previous;

	public  PaginatedCollection(List<T> items) {
		this.items = items;
	}
}