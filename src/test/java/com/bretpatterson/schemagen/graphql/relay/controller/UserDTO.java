package com.bretpatterson.schemagen.graphql.relay.controller;

import com.bretpatterson.schemagen.graphql.relay.INode;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLTypeName;

/**
 * Created by bpatterson on 1/30/16.
 */
@GraphQLTypeName(name="User")
public class UserDTO implements INode {
	private String id;
	private String name;
	private String email;


	@Override
	public String getId() {
		return id;
	}

	public UserDTO setId(String id) {
		this.id = id;

		return this;
	}

	public String getName() {
		return name;
	}

	public UserDTO setName(String name) {
		this.name = name;

		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserDTO setEmail(String email) {
		this.email = email;

		return this;
	}
}
