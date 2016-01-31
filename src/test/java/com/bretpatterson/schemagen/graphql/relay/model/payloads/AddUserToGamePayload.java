package com.bretpatterson.schemagen.graphql.relay.model.payloads;

import com.bretpatterson.schemagen.graphql.relay.controller.GameDTO;

/**
 * Created by bpatterson on 1/30/16.
 */
public class AddUserToGamePayload {
	private GameDTO game;
	private String clientMutationId;

	public GameDTO getGame() {
		return game;
	}

	public AddUserToGamePayload setGame(GameDTO game) {
		this.game = game;

		return this;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public AddUserToGamePayload setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;

		return this;
	}
}
