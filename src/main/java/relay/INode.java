package relay;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeName;

/**
 * All GraphQL types used in relay must implement this interface
 * in order to get support for Relay's node call to retrieve objects.
 */
@GraphQLTypeName(name="Node")
public interface INode {

	String getId();
}
