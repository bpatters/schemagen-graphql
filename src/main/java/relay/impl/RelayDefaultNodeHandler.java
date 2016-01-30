package relay.impl;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.google.common.collect.ImmutableList;
import relay.INode;
import relay.IRelayNodeFactory;
import relay.IRelayNodeHandler;
import relay.exceptions.UnknownObjectType;

import java.util.List;

/**
 * This is the default handler for all node query requests for relay.
 */
@GraphQLController
public class RelayDefaultNodeHandler implements IRelayNodeHandler {
	private
	List<IRelayNodeFactory> nodeFactories;

	RelayDefaultNodeHandler(List< IRelayNodeFactory> nodeFactories) {
		this.nodeFactories = nodeFactories;
	}

	@GraphQLQuery(name="node")
	public INode findNodeById(@GraphQLParam(name="id") String nodeId) {

		for (IRelayNodeFactory factory : nodeFactories) {
			if (factory.handlesNodeId(nodeId)) {
				return factory.newObjectFromID(nodeId);
			}
		}
		throw new UnknownObjectType(String.format("Unable to map id %s to a valid data type", nodeId));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		ImmutableList.Builder<IRelayNodeFactory> nodeFactoriesBuilder = ImmutableList.builder();

		public Builder registerFactory(IRelayNodeFactory factory) {
			nodeFactoriesBuilder.add(factory);

			return this;
		}

		public RelayDefaultNodeHandler build() {
			return new RelayDefaultNodeHandler(nodeFactoriesBuilder.build());
		}

	}
}
