package com.bretpatterson.schemagen.graphql.relay.model.payloads;

/**
 * Created by bpatterson on 1/30/16.
 */
public class AddUserToGameInput {
	private String gameId;
	private String userId;
	private String clientMutationId;

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public void setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;
	}
}
