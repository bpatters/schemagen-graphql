package com.bretpatterson.schemagen.graphql.relay.model.payloads;

/**
 * Created by bpatterson on 1/27/16.
 */
public class DeleteUserPayload {
	private String clientMutationId;

	public DeleteUserPayload() {

	}

	public DeleteUserPayload(String clientMutationId) {
		this.clientMutationId = clientMutationId;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public void setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;
	}
}
