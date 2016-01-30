package com.bretpatterson.schemagen.graphql.relay.manager;

import com.bretpatterson.schemagen.graphql.relay.IGameFactory;
import com.bretpatterson.schemagen.graphql.relay.IUserFactory;
import com.bretpatterson.schemagen.graphql.relay.dao.GameDAO;
import com.bretpatterson.schemagen.graphql.relay.model.IGame;
import com.bretpatterson.schemagen.graphql.relay.model.IUser;
import com.bretpatterson.schemagen.graphql.relay.model.impl.Game;
import com.bretpatterson.schemagen.graphql.relay.model.impl.User;
import com.google.common.base.Optional;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by bpatterson on 1/30/16.
 */
public class GameManager implements IGameFactory, IUserFactory {
	GameDAO gameDAO = new GameDAO();

	public IGame newGame() {
		return new Game();
	}
	public IUser newUser() {
		return new User();
	}

	public List<IGame> findGames(String namePrefix) {
		return gameDAO.findGames(namePrefix);
	}
	public List<IUser> findUsers(String namePrefix) {
		return gameDAO.findUsers(namePrefix);
	}

	public IUser createUser(IUser user) {
		checkNotNull(user.getName());
		checkNotNull(user.getEmail());

		return gameDAO.saveUser(user);
	}
	public IUser updateUser(IUser user) {
		checkNotNull(user.getName());
		checkNotNull(user.getEmail());

		return gameDAO.saveUser(user);
	}
	public void deleteUser(Long id) {
		gameDAO.removeUser(id);
	}

	public IGame createGame(IGame game) {
		checkNotNull(game.getName());

		return gameDAO.saveGame(game);
	}
	public IGame updateGame(IGame game) {
		checkNotNull(game.getId());
		checkNotNull(game.getName());

		return gameDAO.saveGame(game);
	}

	public void deleteGame(Long id) {
		gameDAO.removeGame(id);
	}


	public Optional<IGame> findGame(Long id) {
		return gameDAO.findGame(id);
	}

	public Optional<IUser> findUser(Long id) {
		return gameDAO.findUser(id);
	}
}
