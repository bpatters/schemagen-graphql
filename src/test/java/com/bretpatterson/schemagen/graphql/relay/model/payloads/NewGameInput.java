package com.bretpatterson.schemagen.graphql.relay.model.payloads;

import com.bretpatterson.schemagen.graphql.relay.controller.GameDTO;

/**
 * Created by bpatterson on 1/27/16.
 */
public class NewGameInput {
	private GameDTO game;
	private String clientMutationId;

	public GameDTO getGame() {
		return game;
	}

	public NewGameInput setGame(GameDTO game) {
		this.game = game;

		return this;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public NewGameInput setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;

		return this;
	}
}
