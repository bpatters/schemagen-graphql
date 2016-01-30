package com.bretpatterson.schemagen.graphql.relay.model.payloads;

import com.bretpatterson.schemagen.graphql.relay.controller.GameDTO;

/**
 * Created by bpatterson on 1/27/16.
 */
public class NewGamePayload {
	private GameDTO game;
	private String clientMutationId;

	public NewGamePayload() {

	}

	public NewGamePayload(GameDTO game, String clientMutationId) {
		this.game = game;
		this.clientMutationId = clientMutationId;
	}

	public GameDTO getGame() {
		return game;
	}

	public void setGame(GameDTO game) {
		this.game = game;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public void setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;
	}
}
