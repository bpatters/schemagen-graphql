package com.bretpatterson.schemagen.graphql.relay;

import graphql.relay.ConnectionCursor;

/**
 * Generic type for representing a Relay Edge connection.
 */
public class Edge<T> {
	T node;
	ConnectionCursor cursor;

	public Edge(T node, ConnectionCursor cursor) {
        this.node = node;
        this.cursor = cursor;
    }


    public T getNode() {
        return node;
    }

    public void setNode(T node) {
        this.node = node;
    }

    public ConnectionCursor getCursor() {
        return cursor;
    }

    public void setCursor(ConnectionCursor cursor) {
        this.cursor = cursor;
    }
}
