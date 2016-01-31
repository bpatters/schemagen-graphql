package com.bretpatterson.schemagen.graphql.relay;

/**
 * Interface for node factories that know how to construct objects from the appropriate node id.
 */
public interface IRelayNodeFactory {

	/**
	 * This method should return true only if the specified object id should be handled by this factory
	 */
	boolean handlesNodeId(String id);

	/**
	 * Accepts a Relay ID and constructs an object based on the ID.
	 * @param objectId the relay object ID. All object ID's must be prefixed with the factories name
	 * @return
	 */
	INode newObjectFromID(String objectId);

}
