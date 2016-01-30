package com.bretpatterson.schemagen.graphql.relay.model;

import java.util.List;

/**
 * Created by bpatterson on 1/30/16.
 */
public interface IGame {

	Long getId();

	IGame setId(Long id);

	String getName();

	IGame setName(String name);

	List<IUser> getUsers();

	IGame setUsers(List<IUser> users);
}
