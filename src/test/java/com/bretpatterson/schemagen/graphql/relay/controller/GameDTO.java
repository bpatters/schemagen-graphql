package com.bretpatterson.schemagen.graphql.relay.controller;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLName;
import com.bretpatterson.schemagen.graphql.relay.INode;

import java.util.List;

/**
 * Created by bpatterson on 1/30/16.
 */
@GraphQLName(name="Game")
public class GameDTO implements INode {
	private String id;
	private String name;
	private List<UserDTO> users;

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

	public List<UserDTO> getUsers() {
		return users;
	}

	public GameDTO setUsers(List<UserDTO> users) {
		this.users = users;

		return this;
	}
}
