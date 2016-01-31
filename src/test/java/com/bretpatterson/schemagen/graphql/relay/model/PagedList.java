package com.bretpatterson.schemagen.graphql.relay.model;

import java.util.List;

/**
 * Created by bpatterson on 1/30/16.
 */
public class PagedList<T> {

	private List<T> items;
	private boolean hasPreviousPage;
	private boolean hasNextPage;

	private PagedList(List<T> items, boolean hasPreviousPage, boolean hasNextPage) {
		this.items = items;
		this.setHasPreviousPage(hasPreviousPage);
		this.setHasNextPage(hasNextPage);
	}

	public static <T> PagedList<T> of(List<T> items, boolean hasPreviousPage, boolean hasNextPage) {
		return new PagedList<T>(items, hasPreviousPage, hasNextPage);
	}

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public boolean isHasPreviousPage() {
		return hasPreviousPage;
	}

	public void setHasPreviousPage(boolean hasPreviousPage) {
		this.hasPreviousPage = hasPreviousPage;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}
}
