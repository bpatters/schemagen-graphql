package com.bretpatterson.schemagen.graphql.relay.controller;

import com.bretpatterson.schemagen.graphql.annotations.GraphQLController;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLMutation;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLParam;
import com.bretpatterson.schemagen.graphql.annotations.GraphQLQuery;
import com.bretpatterson.schemagen.graphql.relay.manager.GameManager;
import com.bretpatterson.schemagen.graphql.relay.model.IGame;
import com.bretpatterson.schemagen.graphql.relay.model.IUser;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.DeleteGameInput;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.DeleteGamePayload;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.DeleteUserInput;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.DeleteUserPayload;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.NewGameInput;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.NewGamePayload;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.NewUserInput;
import com.bretpatterson.schemagen.graphql.relay.model.payloads.NewUserPayload;
import com.bretpatterson.schemagen.graphql.relay.model.relay.factories.RelayGameFactory;
import com.bretpatterson.schemagen.graphql.relay.model.relay.factories.RelayUserFactory;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by bpatterson on 1/27/16.
 */
@GraphQLController
public class GameController {

	GameManager gameManager = new GameManager();
	/**
	 * our game factories used by relay
	 *
	 */
	RelayGameFactory gameFactory = new RelayGameFactory(this);
	RelayUserFactory userFactory = new RelayUserFactory(this);

	@GraphQLQuery(name = "games")
	public List<GameDTO> findGames(@GraphQLParam(name="namePrefix") String namePrefix) {
		List<IGame> games = gameManager.findGames(namePrefix);

		List<GameDTO> foundGames = Lists.transform(games, new Function<IGame, GameDTO>() {

			@Override
			public GameDTO apply(IGame input) {
				return convertToGameDTO(input);
			}
		});

		return foundGames;
	}

	@GraphQLQuery(name = "users")
	public List<UserDTO> findUsers(@GraphQLParam(name="namePrefix") String namePrefix) {
		List<IUser> users = gameManager.findUsers(namePrefix);

		List<UserDTO> foundUsers = Lists.transform(users, new Function<IUser, UserDTO>() {

			@Override
			public UserDTO apply(IUser input) {
				return convertToUserDTO(input);
			}
		});

		return foundUsers;
	}

	@GraphQLMutation(name = "createGame")
	public NewGamePayload createGame(@GraphQLParam(name = "gameInput", required = true) NewGameInput newGameInput) {
		checkNotNull(newGameInput.getGame());
		checkNotNull(newGameInput.getGame().getName());
		checkNotNull(newGameInput.getClientMutationId());
		IGame newGame = gameManager.newGame().setName(newGameInput.getGame().getName());

		newGame = gameManager.createGame(newGame);

		return new NewGamePayload(convertToGameDTO(newGame), newGameInput.getClientMutationId());
	}

	@GraphQLMutation(name = "deleteGame")
	public DeleteGamePayload deleteGame(@GraphQLParam(name = "gameInput", required = true) DeleteGameInput game) {
		checkNotNull(game);
		checkNotNull(game.getId());
		checkNotNull(game.getClientMutationId());
		gameManager.deleteGame(gameFactory.getGameId(game.getId()));

		return new DeleteGamePayload(game.getClientMutationId());

	}

	@GraphQLMutation(name = "createUser")
	public NewUserPayload createUser(@GraphQLParam(name = "userInput", required = true) NewUserInput userInput) {
		checkNotNull(userInput);
		checkNotNull(userInput.getUser());
		checkNotNull(userInput.getUser().getEmail());
		checkNotNull(userInput.getUser().getName());
		checkNotNull(userInput.getClientMutationId());

		IUser user = gameManager.newUser().setName(userInput.getUser().getName()).setEmail(userInput.getUser().getEmail());
		IUser newUser = gameManager.createUser(user);

		return new NewUserPayload(convertToUserDTO(newUser), userInput.getClientMutationId());
	}

	@GraphQLMutation(name = "deleteUser")
	public DeleteUserPayload deleteUser(@GraphQLParam(name = "userInput", required = true) DeleteUserInput userInput) {
		checkNotNull(userInput);
		checkNotNull(userInput.getId());
		checkNotNull(userInput.getClientMutationId());
		gameManager.deleteUser(userFactory.getUserId(userInput.getId()));

		return new DeleteUserPayload(userInput.getClientMutationId());
	}

	public GameDTO findGame(Long id) {
		return convertToGameDTO(gameManager.findGame(id).orNull());
	}

	public UserDTO findUser(Long id) {
		return convertToUserDTO(gameManager.findUser(id).orNull());
	}

	private GameDTO convertToGameDTO(IGame game) {
		if (game == null) {
			return null;
		}
		GameDTO rv = new GameDTO().setId(gameFactory.getNodeId(game.getId())).setName(game.getName());

		return rv;

	}

	private UserDTO convertToUserDTO(IUser user) {
		if (user == null) {
			return null;
		}
		UserDTO rv = new UserDTO().setId(userFactory.getNodeId(user.getId())).setName(user.getName()).setEmail(user.getEmail());

		return rv;

	}
}
