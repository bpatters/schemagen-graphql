package com.bretpatterson.schemagen.graphql.relay.dao;

import com.bretpatterson.schemagen.graphql.relay.model.IGame;
import com.bretpatterson.schemagen.graphql.relay.model.IUser;
import com.bretpatterson.schemagen.graphql.relay.model.PagedList;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Simple in memory story for all game objects
 */
public class GameDAO {

	private List<IGame> games = Lists.newArrayList();
	private List<IUser> users = Lists.newArrayList();
	private long lastGameId = 0;
	private long lastUserId = 0;

	public PagedList<IGame> findGames(Optional<Integer> first, Optional<Integer> last, Optional<Long> before, Optional<Long> after) {
		checkArgument(before.isPresent() && before.get() >= 0);
		checkArgument(before.isPresent() && before.get() < games.size());
		checkArgument(after.isPresent() && after.get() >= 0);
		checkArgument(after.isPresent() && after.get() < games.size());
		checkArgument(before.isPresent() || after.isPresent());

		List<IGame> items;
		int start = 0, end = games.size();
		if (before.isPresent()) {
			end = indexOfGame(before.get());
		}
		if (after.isPresent()) {
			start = indexOfGame(after.get());
		}

		if (first.isPresent() && (end - start) > first.get()) {
			// move end to set the size to first
			end = end - (end - start - first.get());
		}
		if (last.isPresent() && (end - start) > last.get()) {
			start = start + (end - start - last.get());
		}
		items = games.subList(start, end);

		return PagedList.of(items, start > 0, end < (games.size() - 1));

	}

	public PagedList<IUser> findUsers(Optional<Integer> first, Optional<Integer> last, Optional<Long> before, Optional<Long> after) {
		checkArgument(before.isPresent() && before.get() >= 0);
		checkArgument(before.isPresent() && before.get() < users.size());
		checkArgument(after.isPresent() && after.get() >= 0);
		checkArgument(after.isPresent() && after.get() < users.size());
		checkArgument(before.isPresent() || after.isPresent());

		List<IUser> items;
		int start = 0, end = users.size();
		if (before.isPresent()) {
			end = indexOfUser(before.get());
		}
		if (after.isPresent()) {
			start = indexOfUser(after.get());
		}

		if (first.isPresent() && (end - start) > first.get()) {
			// move end to set the size to first
			end = end - (end - start - first.get());
		}
		if (last.isPresent() && (end - start) > last.get()) {
			start = start + (end - start - last.get());
		}
		items = users.subList(start, end);

		return PagedList.of(items, start > 0, end < (users.size() - 1));
	}

	public Optional<IGame> findGame(Long id) {
		for (IGame game : games) {
			if (id.equals(game.getId())) {
				return Optional.of(game);
			}
		}
		return Optional.absent();
	}

	public Optional<IUser> findUser(Long id) {
		for (IUser user : users) {
			if (id.equals(user.getId())) {
				return Optional.of(user);
			}
		}
		return Optional.absent();
	}

	public IGame saveGame(IGame game) {
		if (game.getId() == null) {
			game.setId(++lastGameId);
		}
		games.add(game);
		return game;
	}

	public IUser saveUser(IUser user) {
		if (user.getId() == null) {
			user.setId(++lastUserId);
		}
		users.add(user);
		return user;
	}

	public void removeGame(Long id) {
		Optional<IGame> gameToDelete = findGame(id);
		if (!gameToDelete.isPresent()) {
			return;
		}
		games.remove(gameToDelete.get());
	}

	public void removeUser(Long id) {
		Optional<IUser> userToDelete = findUser(id);
		if (!userToDelete.isPresent()) {
			return;
		}
		users.remove(userToDelete.get());
		for (IGame game : games) {
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
		return ImmutableList.copyOf(games);
	}

	public List<IUser> getAllUsers() {
		return ImmutableList.copyOf(users);
	}

	public int indexOfGame(Long gameId) {
		int i = 0;
		for (IGame item : games) {
			if (gameId.equals(item.getId())) {
				return i;
			}
			i++;
		}

		throw new IllegalArgumentException(String.format("Game id %d not found.", gameId));
	}

	public int indexOfUser(Long userId) {
		int i = 0;
		for (IUser item : users) {
			if (userId.equals(item.getId())) {
				return i;
			}
			i++;
		}

		throw new IllegalArgumentException(String.format("User id %d not found.", userId));
	}

}
