package com.bretpatterson.schemagen.graphql.relay.model.payloads;

/**
 * Created by bpatterson on 1/27/16.
 */
public class DeleteUserInput {
	private String id;
	private String clientMutationId;

	public String getId() {
		return id;
	}

	public DeleteUserInput setId(String userId) {
		this.id = userId;

		return this;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public DeleteUserInput setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;

		return this;
	}
}
