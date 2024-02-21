package de.lmu.ifi.sosy.tbial.core.game;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.core.cards.Character;
import de.lmu.ifi.sosy.tbial.core.cards.Role;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.core.player.PlayerFactory;
import de.lmu.ifi.sosy.tbial.core.player.Type;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.UserService;
import de.lmu.ifi.sosy.tbial.core.user.UserViewModel;
import de.lmu.ifi.sosy.tbial.security.user.AuthenticatedUser;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.dialogs.PasswordDialog;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@CssImport(value = "./styles/invitation.css")
public class GameViewModel {
  Logger logger = LoggerFactory.getLogger(GameViewModel.class);
  private final GameService gameService;
  private final UserService userService;
  private final AuthenticatedUser authenticatedUser;
  private final PlayerFactory playerFactory;
  private String currentGameName = "";
  private final UserViewModel userViewModel;

  public GameViewModel(
      @Autowired GameService gameService,
      @Autowired AuthenticatedUser authenticatedUser,
      @Autowired PlayerFactory playerFactory,
      @Autowired UserViewModel userViewModel,
      @Autowired UserService userService) {
    this.gameService = gameService;
    this.authenticatedUser = authenticatedUser;
    this.playerFactory = playerFactory;
    this.userViewModel = userViewModel;
    this.userService = userService;
  }

  public boolean isInputValid(String name, boolean isLocked, String password) {
    boolean isNameValid = name != null && !name.isBlank();
    boolean isPasswordValid = !isLocked || (password != null && !password.isBlank());
    return isNameValid && isPasswordValid;
  }

  public boolean doesGameExist(String gameName) {
    return gameService.findBy(gameName) != null;
  }

  public void createGame(
      String name, int maxPlayers, boolean isLocked, String password, Status status) {
    try {
      Game game =
          new Game(name, maxPlayers, isLocked, password, status, getCurrentUser().getUsername());
      // Do nothing if the game already exists
      if (getAllGames().contains(game)) {
        return;
      }
      currentGameName = name;
      Player player = playerFactory.createPlayer(getCurrentUser().getUsername(), Type.HOST);
      game.add(player);
      userViewModel.setAvailable(false);
      gameService.save(game);

      Broadcaster.broadcast(new ViewData<>(userViewModel.getAvailableUsers(), ViewData.Type.USERS));
      logger.info("New game " + game.getName() + " successfully created.");
    } catch (NoSuchElementException e) {
      logger.error("There is currently no signed-in user.", e);
    }
  }

  public void joinGame(String gameName) {
    Game selectedGame = gameService.findBy(gameName);
    String currentUsername = getCurrentUser().getUsername();
    currentGameName = gameName;
    Optional<Player> userFound =
        selectedGame.getPlayers().stream()
            .filter(player -> player.getUsername().equals(currentUsername))
            .findFirst();

    if (userFound.isEmpty()) {
      if (selectedGame.canBeJoined()) {
        // User is allowed to join
        if (selectedGame.isLocked()) {
          Dialog passwordDialog = new PasswordDialog(this, selectedGame.getPassword());
          passwordDialog.open();
        } else {
          addPlayerToGame(selectedGame, currentUsername);
          Broadcaster.broadcast(new ViewData<>(getAllGames(), ViewData.Type.GAMES));
          Broadcaster.broadcast(
              new ViewData<>(Collections.emptyList(), ViewData.Type.NOTIFICATION_JOINED));
        }
      } else {
        showGameIsFullNotification();
      }
    }
  }

  public void addPlayerToGame(Game game, String playerName) {
    Player player = playerFactory.createPlayer(playerName, Type.PLAYER);
    game.add(player);
    userViewModel.setAvailable(false);
    gameService.save(game);
    Broadcaster.broadcast(new ViewData<>(userViewModel.getAvailableUsers(), ViewData.Type.USERS));
  }

  /**
   * Returns the authenticated user if there is any, otherwise throws an exception.
   *
   * @return The currently signed-in user
   * @throws NoSuchElementException if there is no signed-in user
   */
  public User getCurrentUser() {
    return authenticatedUser.get().orElseThrow();
  }

  public Optional<Game> getCurrentGame() {
    try {
      if (getCurrentUser().isAvailable()) {
        return Optional.ofNullable(gameService.findBy(currentGameName));
      } else {
        for (Game game : getAllGames()) {
          Optional<Player> maybePlayer =
              game.getPlayers().stream()
                  .filter(player -> player.getUsername().equals(getCurrentUser().getUsername()))
                  .findFirst();
          if (maybePlayer.isPresent()) {
            return Optional.of(game);
          }
        }
      }
    } catch (NoSuchElementException e) {
      logger.error("There is currently no signed-in user.");
    }
    return Optional.empty();
  }

  public void endTurn() {
    getCurrentGame().ifPresent(Game::changeTurn);
  }

  public Optional<Player> getPlayerOnTurn() {
    return getCurrentGame()
        .flatMap(
            game ->
                Optional.ofNullable(
                    new ArrayList<>(game.getPlayers()).get(game.getCurrentTurnIndex())));
  }

  private void setManagerStartsFirst(Game game) {
    ArrayList<Player> all = new ArrayList<>(game.getPlayers());
    // Find the manager and let them be their first turn.
    Optional<Player> maybeManager =
        all.stream().filter(player -> player.getRole() == Role.MANAGER).findFirst();
    maybeManager.ifPresent(manager -> game.setCurrentTurnIndex(all.indexOf(manager)));
  }

  /**
   * After a game has started, the currently logged-in user in this game is the current player.
   *
   * @return The player whose username is the same as the one of the currently logged-in user
   */
  public Optional<Player> getCurrentPlayer() {
    if (getCurrentGame().isPresent()) {
      return getCurrentGame().get().getPlayers().stream()
          .filter(player -> player.getUsername().equals(getCurrentUser().getUsername()))
          .findFirst();
    }
    return Optional.empty();
  }

  public void leaveGame() {
    getCurrentGame()
        .ifPresent(
            game -> {
              Set<Player> players = game.getPlayers();
              ArrayList<Player> all = new ArrayList<>(game.getPlayers());

              Optional<Player> self =
                  players.stream()
                      .filter(player -> getCurrentUser().getUsername().equals(player.getUsername()))
                      .findFirst();
              if (self.isPresent()) {
                Player player = self.get();
                // Remove the player from the game
                players.remove(player);

                if (game.getStatus() == Status.STARTED) {
                  // Change the turn to the next player
                  if (all.indexOf(player) == game.getCurrentTurnIndex()) {
                    // The player who left was on turn
                    game.setCurrentTurnIndex(all.indexOf(player) % game.getPlayersCount());
                    // The next player draws two cards
                    Player next =
                        new ArrayList<>(game.getPlayers()).get(game.getCurrentTurnIndex());
                    drawCard(game, next);
                    drawCard(game, next);
                  }
                }
                userViewModel.setAvailable(true);

                // Make other player the host
                if (players.size() > 0) {
                  if (player.getType() == Type.HOST) {
                    Player random = getRandomPlayer(players);
                    random.setType(Type.HOST);
                  }
                  // Workaround: only save if game is not started
                  if (game.getStatus() == Status.READY) {
                    gameService.save(game);
                  }
                } else {
                  gameService.delete(game);
                }
              }
            });
  }

  public void drawCard(Game game, Player onTurn) {
    game.drawFromDeck(onTurn).ifPresent(onTurn::addToHand);
  }

  public void startGame() {
    getCurrentGame()
        .ifPresent(
            game -> {
              if (game.getStatus() != Status.STARTED) {
                if (game.getPlayersCount() >= 4) {
                  distributeRoles(game.getPlayers());
                  logger.info("All " + game.getPlayersCount() + " players got assigned a role.");

                  distributeCharacters(game.getPlayers());
                  logger.info(
                      "All " + game.getPlayersCount() + " players got assigned a character.");

                  distributeCards(game.getPlayers());
                  logger.info("The players took cards from the deck.");

                  setManagerStartsFirst(game);

                  // Broadcast a msg that the game has started
                  game.setStatus(Status.STARTED);
                  Broadcaster.broadcast(new ViewData<>(getAllGames(), ViewData.Type.GAMES));

                  logger.info("Game " + game.getName() + " has started.");
                } else {
                  Notification.show(
                      "Game can't start with " + game.getPlayersCount() + " players.",
                      3500,
                      Notification.Position.BOTTOM_STRETCH);
                }
              }
            });
  }

  private void distributeCards(Set<Player> players) {
    getCurrentGame()
        .ifPresent(
            currentGame -> {
              Stack<Card> deck = Helpers.getDeck();
              players.forEach(
                  player -> {
                    for (int i = 0; i < player.getHealthPoints(); i++) {
                      player.addToHand(deck.pop());
                    }
                  });
              currentGame.setDeck(deck);
              currentGame.setDiscardPile(new ArrayList<>());
            });
  }

  private void distributeRoles(Set<Player> players) {
    List<Role> roles = new ArrayList<>();

    switch (players.size()) {
      case 4:
        assignRole(players, roles);
        break;
      case 5:
        roles.add(Role.HONEST_DEVELOPER);
        assignRole(players, roles);
        break;
      case 6:
        roles.add(Role.EVIL_CODE_MONKEY);
        roles.add(Role.HONEST_DEVELOPER);
        assignRole(players, roles);
        break;
      case 7:
        roles.add(Role.EVIL_CODE_MONKEY);
        roles.add(Role.HONEST_DEVELOPER);
        roles.add(Role.HONEST_DEVELOPER);
        assignRole(players, roles);
        break;
      default:
    }
  }

  private void assignRole(Set<Player> players, List<Role> roles) {
    roles.add(Role.MANAGER);
    roles.add(Role.CONSULTANT);
    roles.add(Role.EVIL_CODE_MONKEY);
    roles.add(Role.EVIL_CODE_MONKEY);

    players.forEach(
        player -> {
          Role randomRole = roles.get(new Random().nextInt(roles.size()));
          player.setRole(randomRole);

          if (randomRole == Role.MANAGER) player.setHealthPoints(5);

          logger.info(player.getUsername() + " was assigned the role " + randomRole.getName());
          roles.remove(randomRole);
        });
  }

  private void distributeCharacters(Set<Player> players) {
    List<Character> characters = new ArrayList<>(List.of(Character.values()));
    players.forEach(
        player -> {
          Character randomCharacter = characters.get(new Random().nextInt(characters.size()));
          player.setCharacter(randomCharacter);
          logger.info(
              player.getUsername() + " was assigned the character " + randomCharacter.getName());
          characters.remove(randomCharacter);
        });
  }

  private Player getRandomPlayer(Set<Player> playerSet) {
    // Convert current players to ArrayList
    ArrayList<Player> players = new ArrayList<>(playerSet);
    int randomNumber = new Random().nextInt(players.size()); // [0,size)
    // Because there is no NullException in the Set players, we can directly get the item
    return players.get(randomNumber);
  }

  public List<Game> getAllGames() {
    return gameService.getGames();
  }

  public void showJoinNotification() {
    getCurrentGame()
        .ifPresent(
            game -> {
              Optional<Player> host =
                  game.getPlayers().stream()
                      .filter(
                          player ->
                              player.getType() == Type.HOST
                                  && getCurrentUser().getUsername().equals(player.getUsername())
                                  && game.getPlayersCount() > 1)
                      .findFirst();

              if (host.isPresent()) {
                Notification.show(
                    "Someone joined your game!", 3500, Notification.Position.BOTTOM_STRETCH);
              }
            });
  }

  public void kickPlayer(String username) {
    getCurrentGame()
        .ifPresent(
            game -> {
              Set<Player> players = game.getPlayers();
              players.removeIf(player -> player.getUsername().equals(username));
              User kickedOne = userViewModel.findBy(username);
              kickedOne.setAvailable(true);
              gameService.save(game);
              userService.save(kickedOne);
              Broadcaster.broadcast(
                  new ViewData<>(userViewModel.getAvailableUsers(), ViewData.Type.USERS));
            });
  }

  public List<Game> filter(
      List<Game> games,
      String gameName,
      String hasPassword,
      String hasCapacity,
      String maxPlayers,
      String status) {
    List<Game> gamesFilteredList = new ArrayList<>(games);
    if (gameName != null) {
      gamesFilteredList = filterByName(games, gameName);
    }
    if (hasPassword != null) {
      gamesFilteredList = filterByPassword(gamesFilteredList, hasPassword);
    }
    if (hasCapacity != null) {
      gamesFilteredList = filterByCapacity(gamesFilteredList, hasCapacity);
    }
    if (maxPlayers != null) {
      gamesFilteredList = filterByMaxPlayers(gamesFilteredList, maxPlayers);
    }
    if (status != null) {
      gamesFilteredList = filterByStatus(gamesFilteredList, status);
    }
    return gamesFilteredList;
  }

  private List<Game> filterByName(List<Game> games, String filterValue) {
    List<Game> gamesFilteredName = new ArrayList<>();
    for (Game game : games) {
      if (game.getName().contains(filterValue)) {
        gamesFilteredName.add(game);
      }
    }
    return gamesFilteredName;
  }

  private List<Game> filterByPassword(List<Game> games, String filterValue) {
    List<Game> gamesFilteredPassword = new ArrayList<>();
    if (Objects.equals(filterValue, "yes")) {
      for (Game game : games) {
        if (game.isLocked()) {
          gamesFilteredPassword.add(game);
        }
      }
    } else if (Objects.equals(filterValue, "no")) {
      for (Game game : games) {
        if (!game.isLocked()) {
          gamesFilteredPassword.add(game);
        }
      }
    } else {
      return games;
    }
    return gamesFilteredPassword;
  }

  private List<Game> filterByCapacity(List<Game> games, String filterValue) {
    List<Game> gamesFilteredCapacity = new ArrayList<>();
    if (Objects.equals(filterValue, "yes")) {
      for (Game game : games) {
        if (game.getPlayersCount() < game.getMaxPlayers()) {
          gamesFilteredCapacity.add(game);
        }
      }
    } else if (Objects.equals(filterValue, "no")) {
      for (Game game : games) {
        if (game.getPlayersCount() >= game.getMaxPlayers()) {
          gamesFilteredCapacity.add(game);
        }
      }
    } else {
      return games;
    }
    return gamesFilteredCapacity;
  }

  private List<Game> filterByMaxPlayers(List<Game> games, String filterValue) {
    List<Game> gamesFilteredMaxPlayers = new ArrayList<>();
    for (Game game : games) {
      if (game.getMaxPlayers() == Integer.parseInt(filterValue)) {
        gamesFilteredMaxPlayers.add(game);
      }
    }
    return gamesFilteredMaxPlayers;
  }

  private List<Game> filterByStatus(List<Game> games, String filterValue) {
    List<Game> gamesFilteredStatus = new ArrayList<>();
    for (Game game : games) {
      if (Objects.equals(game.getStatus().toString(), filterValue)) {
        gamesFilteredStatus.add(game);
      }
    }
    return gamesFilteredStatus;
  }

  public void showGameIsFullNotification() {
    getCurrentGame()
        .ifPresent(
            game ->
                Notification.show(
                    "Sorry :( This game is already full of players.",
                    3500,
                    Notification.Position.BOTTOM_STRETCH));
  }

  private Optional<Game> getGameOfInviter(String inviter) {
    for (Game game : getAllGames()) {
      Optional<Player> maybePlayer =
          game.getPlayers().stream()
              .filter(player -> player.getUsername().equals(inviter))
              .findFirst();
      if (maybePlayer.isPresent()) {
        return Optional.of(game);
      }
    }
    return Optional.empty();
  }

  public void showInvitationNotification() {
    String inviterName = userViewModel.getInviterUsername();
    String inviteeName = userViewModel.getInviteeUsername();

    if (getCurrentUser().getUsername().equals(inviteeName)
        && userViewModel.findBy(inviterName) != null) {
      createNotification(inviterName).open();
    }
  }

  private Notification createNotification(String inviterName) {
    HorizontalLayout layout = new HorizontalLayout();
    Notification notification = new Notification();

    String notificationText =
        "User '" + inviterName + "' would like to invite you to a game! Click to join!";
    Div text = new Div(new Text(notificationText));
    text.addClassNames("layout");

    Button join = new Button("Join");
    join.addClassNames("layout");

    attachJoinAction(notification, join, inviterName);

    layout.add(text, join);
    notification.add(layout);
    notification.setDuration(10000);
    notification.setPosition(Notification.Position.BOTTOM_STRETCH);

    return notification;
  }

  private void attachJoinAction(Notification notification, Button join, String inviterName) {
    join.addClickListener(
        event ->
            getGameOfInviter(inviterName)
                .ifPresent(
                    game -> {
                      if (game.canBeJoined()) {
                        if (game.isLocked()) {
                          Dialog passwordDialog = new PasswordDialog(this, game.getPassword());
                          passwordDialog.open();
                        } else {
                          addPlayerToGame(game, getCurrentUser().getUsername());
                          Broadcaster.broadcast(new ViewData<>(getAllGames(), ViewData.Type.GAMES));
                          Broadcaster.broadcast(
                              new ViewData<>(
                                  Collections.emptyList(), ViewData.Type.NOTIFICATION_JOINED));
                          join.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY));
                        }
                        notification.close();
                      } else {
                        // Show game is full notification
                        showGameIsFullNotification();
                        notification.close();
                      }
                    }));
  }
}
