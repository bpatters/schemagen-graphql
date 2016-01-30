package relay;

import graphql.relay.PageInfo;

import java.util.List;

/**
 * Generic class used for representing Relay Connection Objects
 */
public class RelayConnection<T> {
	private List<Edge<T>> edges;
	private PageInfo pageInfo;

	public List<Edge<T>> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge<T>> edges) {
		this.edges = edges;
	}

	public PageInfo getPageInfo() {
		return pageInfo;
	}

	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
}
