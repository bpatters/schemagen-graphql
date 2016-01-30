package com.bretpatterson.schemagen.graphql.relay.model.payloads;

import com.bretpatterson.schemagen.graphql.relay.controller.UserDTO;

/**
 * Created by bpatterson on 1/27/16.
 */
public class NewUserPayload {
	private UserDTO user;
	private String clientMutationId;

	public NewUserPayload() {

	}

	public NewUserPayload(UserDTO user, String clientMutationId) {
		this.user = user;
		this.clientMutationId = clientMutationId;
	}

	public UserDTO getUser() {
		return user;
	}

	public void setUser(UserDTO user) {
		this.user = user;
	}

	public String getClientMutationId() {
		return clientMutationId;
	}

	public void setClientMutationId(String clientMutationId) {
		this.clientMutationId = clientMutationId;
	}
}
