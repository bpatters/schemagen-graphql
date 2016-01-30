package com.bretpatterson.schemagen.graphql.relay.controller;

import relay.INode;
import relay.RelayConnection;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeName;

/**
 * Created by bpatterson on 1/30/16.
 */
@GraphQLTypeName(name="Game")
public class GameDTO implements INode {
	private String id;
	private String name;
	private RelayConnection<UserDTO> users;

	@Override
	public String getId() {
		return id;
	}

	public GameDTO setId(String id) {
		this.id = id;

		return this;
	}

	public String getName() {
		return name;
	}

	public GameDTO setName(String name) {
		this.name = name;
		return this;
	}

	public RelayConnection<UserDTO> getUsers() {
		return users;
	}

	public GameDTO setUsers(RelayConnection<UserDTO> users) {
		this.users = users;

		return this;
	}
}
