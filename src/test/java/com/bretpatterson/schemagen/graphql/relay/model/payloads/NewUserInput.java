package com.bretpatterson.schemagen.graphql.relay.model.payloads;

import com.bretpatterson.schemagen.graphql.relay.controller.UserDTO;

/**
 * Created by bpatterson on 1/27/16.
 */
public class NewUserInput {
	private UserDTO user;
	private String clientMutationId;

	public UserDTO getUser() {
		return user;
	}

	public NewUserInput setUser(UserDTO user) {
		this.user = user;

		return this;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public NewUserInput setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;

		return this;
	}
}
