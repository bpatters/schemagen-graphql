package com.bretpatterson.schemagen.graphql.relay;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLName;

/**
 * All GraphQL types used in relay must implement this interface
 * in order to get support for Relay's node call to retrieve objects.
 */
@GraphQLName(name="Node")
public interface INode {

	String getId();
}
