package com.bretpatterson.schemagen.graphql.relay.model.impl;

import com.bretpatterson.schemagen.graphql.relay.model.IUser;

/**
 * Created by bpatterson on 1/27/16.
 */
public class User implements IUser {
	private Long id;
	private String name;
	private String email;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public IUser setId(Long id) {
		this.id = id;

		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IUser setName(String name) {
		this.name = name;

		return this;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public IUser setEmail(String email) {
		this.email = email;

		return this;
	}
}
