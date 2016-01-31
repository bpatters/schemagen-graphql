package com.bretpatterson.schemagen.graphql.relay;

/**
 * A GrapphQLController annotated object that handles Relay node(id: string) requests
 * to the server.
 */
public interface IRelayNodeHandler {

	/**
	 * This method finds an existing object by it's node id. The should delegate to the registered
	 * IRelayNodeFactory appropriate for this node type.
	 * @param id
	 * @return
	 */
	INode findNodeById(String id);
}
