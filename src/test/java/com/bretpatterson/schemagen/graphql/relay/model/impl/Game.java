package com.bretpatterson.schemagen.graphql.relay.model.impl;

import com.bretpatterson.schemagen.graphql.relay.model.IGame;
import com.bretpatterson.schemagen.graphql.relay.model.IUser;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by bpatterson on 1/27/16.
 */
public class Game implements IGame {
	private Long id;
	private String name;
	private List<IUser> users;

	public Game() {
		this.users = Lists.newArrayList();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public IGame setId(Long id) {
		this.id = id;

		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IGame setName(String name) {
		this.name = name;

		return this;
	}

	@Override
	public List<IUser> getUsers() {
		return users;
	}

	@Override
	public IGame setUsers(List<IUser> users) {
		this.users = users;

		return this;
	}
}
