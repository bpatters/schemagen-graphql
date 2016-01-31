package com.bretpatterson.schemagen.graphql.relay.model.relay.factories;

import com.bretpatterson.schemagen.graphql.relay.controller.GameController;
import com.bretpatterson.schemagen.graphql.relay.controller.GameDTO;
import com.bretpatterson.schemagen.graphql.relay.model.impl.Game;
import com.bretpatterson.schemagen.graphql.relay.IRelayNodeFactory;
import com.bretpatterson.schemagen.graphql.relay.annotations.RelayNodeFactory;

/**
 * Created by bpatterson on 1/27/16.
 */
@RelayNodeFactory(types={Game.class})
public class RelayGameFactory implements IRelayNodeFactory {
	private static final String NODE_PREFIX = "game:";
	GameController gameController;

	public RelayGameFactory(GameController gameController) {
		this.gameController = gameController;
	}

	@Override
	public boolean handlesNodeId(String id) {
		return id.startsWith(NODE_PREFIX);
	}

	@Override
	public GameDTO newObjectFromID(String objectId) {
		return gameController.findGame(getGameId(objectId));
	}

	public String getNodeId(Long id) {
		return String.format("%s%d",NODE_PREFIX,id);
	}

	public Long getGameId(String id) {
		String gameId =  id.replace(NODE_PREFIX,"");

		return Long.parseLong(gameId);
	}

}
