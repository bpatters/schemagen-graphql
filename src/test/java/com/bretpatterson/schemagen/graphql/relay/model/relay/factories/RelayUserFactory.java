package com.bretpatterson.schemagen.graphql.relay.model.relay.factories;

import com.bretpatterson.schemagen.graphql.relay.IRelayNodeFactory;
import com.bretpatterson.schemagen.graphql.relay.annotations.RelayNodeFactory;

import com.bretpatterson.schemagen.graphql.relay.controller.GameController;
import com.bretpatterson.schemagen.graphql.relay.controller.UserDTO;
import com.bretpatterson.schemagen.graphql.relay.model.impl.User;

/**
 * Factory that knows how to turn node ids into User objects
 */
@RelayNodeFactory(types = { User.class })
public class RelayUserFactory implements IRelayNodeFactory {
	private static final String NODE_PREFIX = "user:";

	GameController gameController;

	public RelayUserFactory(GameController gameController) {
		this.gameController = gameController;
	}

	@Override
	public UserDTO newObjectFromID(String objectId) {
		return gameController.findUser(getUserId(objectId));
	}

	@Override
	public boolean handlesNodeId(String id) {
		return id.startsWith(NODE_PREFIX);
	}

	public String getNodeId(Long id) {
		return String.format("%s%d", NODE_PREFIX, id);
	}


	public Long getUserId(String id) {
		String userId = id.replace(NODE_PREFIX, "");

		return Long.parseLong(userId);
	}
}
