package com.bretpatterson.schemagen.graphql.relay.model.payloads;

/**
 * Created by bpatterson on 1/27/16.
 */
public class DeleteGameInput {
	private String id;
	private String clientMutationId;

	public String getId() {
		return id;
	}

	public void setId(String gameId) {
		this.id = gameId;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public void setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;
	}
}
