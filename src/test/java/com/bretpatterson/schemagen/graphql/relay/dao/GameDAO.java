package com.bretpatterson.schemagen.graphql.relay.dao;

import com.bretpatterson.schemagen.graphql.relay.model.IGame;
import com.bretpatterson.schemagen.graphql.relay.model.IUser;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Simple in memory story for all game objects
 */
public class GameDAO {

	private Map<Long, IGame> games = Maps.newConcurrentMap();
	private Map<Long, IUser> users = Maps.newConcurrentMap();
	private long lastGameId = 0;
	private long lastUserId = 0;

	public List<IGame> findGames(String namePrefix) {
		ImmutableList.Builder<IGame> foundGames = ImmutableList.builder();

		for (IGame game : games.values()) {
			if (game.getName().startsWith(namePrefix)) {
				foundGames.add(game);
			}
		}

		return foundGames.build();
	}

	public List<IUser> findUsers(String namePrefix) {
		ImmutableList.Builder<IUser> foundUser = ImmutableList.builder();

		for (IUser user : users.values()) {
			if (user.getName().startsWith(namePrefix)) {
				foundUser.add(user);
			}
		}

		return foundUser.build();
	}

	public Optional<IGame> findGame(Long id) {
		if (games.containsKey(id)) {
			return Optional.of(games.get(id));
		} else {
			return Optional.absent();
		}
	}

	public Optional<IUser> findUser(Long id) {
		if (users.containsKey(id)) {
			return Optional.of(users.get(id));
		} else {
			return Optional.absent();
		}
	}

	public IGame saveGame(IGame game) {
		if (game.getId() == null) {
			game.setId(++lastGameId);
		}
		games.put(game.getId(), game);
		return game;
	}

	public IUser saveUser(IUser user) {
		if (user.getId() == null) {
			user.setId(++lastUserId);
		}
		users.put(user.getId(), user);
		return user;
	}

	public void removeGame(Long id) {
		games.remove(id);
	}

	public void removeUser(Long id) {
		users.remove(id);
		for (IGame game : games.values()) {
			Iterator<IUser> iter = game.getUsers().iterator();
			while (iter.hasNext()) {
				IUser user = iter.next();
				if (id.equals(user.getId())) {
					iter.remove();
					break;
				}
			}
		}
	}

	public List<IGame> getAllGames() {
		return ImmutableList.copyOf(games.values());
	}

	public List<IUser> getAllUsers() {
		return ImmutableList.copyOf(users.values());
	}

}
