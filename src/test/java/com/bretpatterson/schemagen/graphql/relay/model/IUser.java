package com.bretpatterson.schemagen.graphql.relay.model;

/**
 * Represents a game user
 */
public interface IUser {

	Long getId();

	IUser setId(Long id);

	String getName();

	IUser setName(String name);

	String getEmail();

	IUser setEmail(String email);
}
