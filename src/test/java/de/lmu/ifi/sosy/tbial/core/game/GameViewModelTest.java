package de.lmu.ifi.sosy.tbial.core.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.vaadin.flow.component.UI;
import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.core.cards.Role;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.core.player.PlayerFactory;
import de.lmu.ifi.sosy.tbial.core.player.Type;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.UserService;
import de.lmu.ifi.sosy.tbial.core.user.UserViewModel;
import de.lmu.ifi.sosy.tbial.security.user.AuthenticatedUser;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameViewModelTest {

  @Mock private GameService mockService;
  @Mock private AuthenticatedUser mockAuthenticatedUser;
  @Mock private PlayerFactory mockPlayerFactory;
  @Mock private Player mockPlayer;
  @Mock private Game mockGame;
  @Mock private UserViewModel mockUserViewModel;
  @Mock private User mockUser;
  @Mock private UserService mockUserService;
  private User signedInUser;
  private Game gameOfSignedInUser;
  private GameViewModel viewModel;

  @Mock private UI mockUI;

  @BeforeEach
  public void setUp() {
    signedInUser = createTestUser("john_doe");
    gameOfSignedInUser = createTestGame();
    Mockito.lenient().when(mockAuthenticatedUser.get()).thenReturn(Optional.of(signedInUser));
    UI.setCurrent(mockUI);
    Mockito.lenient().when(mockUI.getUIId()).thenReturn(42);
    viewModel =
        new GameViewModel(
            mockService,
            mockAuthenticatedUser,
            mockPlayerFactory,
            mockUserViewModel,
            mockUserService);
  }

  @Test
  public void isInputValid_with_correct_values_successful() {
    boolean result = viewModel.isInputValid("Name", false, "");
    assertTrue(result);
    result = viewModel.isInputValid("Name", true, "Secret");
    assertTrue(result);
  }

  @Test
  public void isInputValid_with_incorrect_values_fails() {
    boolean result = viewModel.isInputValid("", false, null);
    assertFalse(result);
    result = viewModel.isInputValid("Name", true, "");
    assertFalse(result);
  }

  @Test
  public void whenGetCurrentGame_then_successful() {
    // The current game is ONLY the game of the currently signed-in user.
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    Optional<Game> result = viewModel.getCurrentGame();
    assertTrue(result.isPresent());
    assertEquals(result.get(), gameOfSignedInUser);
  }

  @Test
  public void whenCreateGame_gameDoesNotExist_thenGameSaved() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    Mockito.when(mockPlayerFactory.createPlayer(signedInUser.getUsername(), Type.HOST))
        .thenReturn(mockPlayer);

    viewModel.createGame("CoffeeHouse", 4, true, "password", Status.READY);

    Mockito.verify(mockService, Mockito.atMostOnce()).save(Mockito.any());
  }

  @Test
  public void whenCreateGame_gameExists_thenIgnored() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);

    viewModel.createGame("Starbucks", 5, false, "", Status.READY);

    Mockito.verify(mockPlayerFactory, Mockito.never()).createPlayer(Mockito.any(), Mockito.any());
    Mockito.verify(mockService, Mockito.never()).save(Mockito.any());
  }

  @Test
  public void playerInGame_whenJoinGame_thenIgnored() {
    Set<Player> players = createTestPlayers();
    Mockito.when(mockService.findBy(Mockito.any())).thenReturn(mockGame);
    Mockito.when(mockGame.getPlayers()).thenReturn(players);

    viewModel.joinGame("Starbucks");

    Mockito.verify(mockPlayerFactory, Mockito.never()).createPlayer(Mockito.any(), Mockito.any());
    Mockito.verify(mockService, Mockito.never()).save(Mockito.any());
  }

  @Test
  public void playerNotInGame_gameNotFull_whenJoinGame_thenPlayerJoins() {
    Mockito.when(mockService.findBy(Mockito.any())).thenReturn(mockGame);
    Mockito.when(mockGame.getPlayers()).thenReturn(Collections.emptySet());
    Mockito.when(mockGame.canBeJoined()).thenReturn(true);

    viewModel.joinGame("Starbucks");

    Mockito.verify(mockPlayerFactory, Mockito.times(1)).createPlayer(Mockito.any(), Mockito.any());
    Mockito.verify(mockService, Mockito.times(1)).save(Mockito.any());
  }

  @Test
  public void playerNotInGame_gameFull_whenJoinGame_thenIgnored() {
    Mockito.when(mockService.findBy(Mockito.any())).thenReturn(mockGame);
    Mockito.when(mockGame.getPlayers()).thenReturn(Collections.emptySet());

    viewModel.joinGame("Starbucks");

    Mockito.verify(mockPlayerFactory, Mockito.times(0)).createPlayer(Mockito.any(), Mockito.any());
    Mockito.verify(mockService, Mockito.times(0)).save(Mockito.any());
  }

  @Test
  public void whenFilteredByName_thenSuccessful() {
    List<Game> games = createTestGames();

    List<Game> result = viewModel.filter(games, "St", null, null, null, null);

    result.forEach(game -> assertEquals("Starbucks", gameOfSignedInUser.getName()));
  }

  @Test
  public void whenFilteredByPassword_thenSuccessful() {
    List<Game> games = createTestGames();

    List<Game> result = viewModel.filter(games, null, "yes", null, null, null);

    result.forEach(game -> assertTrue(game.isLocked()));
  }

  @Test
  public void whenFilteredByCapacity_thenSuccessful() {
    List<Game> games = createTestGames();

    List<Game> result = viewModel.filter(games, null, null, "yes", null, null);

    result.forEach(game -> assertNotEquals(game.getPlayersCount(), game.getMaxPlayers()));
  }

  @Test
  public void whenFilteredByMaxPlayers_thenSuccessful() {
    List<Game> games = createTestGames();

    List<Game> result = viewModel.filter(games, null, null, null, "5", null);

    result.forEach(game -> assertEquals(game.getMaxPlayers(), 5));
  }

  @Test
  public void whenFilteredByStatus_thenSuccessful() {
    List<Game> games = createTestGames();

    List<Game> result =
        viewModel.filter(games, null, null, null, null, String.valueOf(Status.PAUSED));

    result.forEach(game -> assertEquals(game.getStatus(), Status.PAUSED));
  }

  @Test
  public void onlyOnePlayerInLobby_whenHostLeavesTheLobby_thenDeleteTheGame() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Because the game of the logged-in user (getTestGame()) currently has 2 players
    gameOfSignedInUser.getPlayers().remove(createTestPlayer("amy", Type.PLAYER));

    viewModel.leaveGame();

    verify(mockService, times(1)).delete(any());
  }

  @Test
  public void manyPlayersInLobby_whenHostLeavesTheLobby_thenChangeTheHost() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);

    viewModel.leaveGame(); // Currently signed-in user leaves the game, amy stays and becomes host

    Optional<Player> maybeHost =
        gameOfSignedInUser.getPlayers().stream()
            .filter(player -> player.getType() == Type.HOST)
            .findFirst();

    assertFalse(gameOfSignedInUser.getPlayers().contains(createTestPlayer("john_doe", Type.HOST)));
    maybeHost.ifPresent(host -> assertNotEquals(signedInUser.getUsername(), host.getUsername()));
  }

  @Test
  public void whenPlayerLeavesTheLobby_thenHostStaysTheSame() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);

    // Make amy the currently logged-in user
    User amy = createTestUser("amy");
    Mockito.lenient().when(mockAuthenticatedUser.get()).thenReturn(Optional.of(amy));

    viewModel.leaveGame(); // amy (as player) leaves the game

    assertTrue(gameOfSignedInUser.getPlayers().contains(createTestPlayer("john_doe", Type.HOST)));
    assertFalse(gameOfSignedInUser.getPlayers().contains(createTestPlayer("amy", Type.PLAYER)));
  }

  @Test
  public void whenKickSomeone_thenRemoveFromPlayers() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    Mockito.when(mockUserViewModel.findBy(Mockito.any())).thenReturn(mockUser);

    viewModel.kickPlayer("amy");

    assertTrue(gameOfSignedInUser.getPlayers().contains(createTestPlayer("john_doe", Type.HOST)));
    assertFalse(gameOfSignedInUser.getPlayers().contains(createTestPlayer("amy", Type.PLAYER)));
  }

  @Test
  public void lessThanFourPlayers_whenStartGame_thenFails() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);

    viewModel.startGame();

    assertNotSame(
        gameOfSignedInUser.getStatus(),
        Status.STARTED); // gameOfSignedInUser is among games and has only 2 players
  }

  @Test
  public void whenStartGame_thenAllPlayersHaveRoles() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    // All players have roles
    gameOfSignedInUser.getPlayers().forEach(player -> assertNotNull(player.getRole()));
    // There is at least one manager
    assertNotNull(
        gameOfSignedInUser.getPlayers().stream()
            .filter(player -> player.getRole() == Role.MANAGER));
  }

  @Test
  public void whenStartGame_thenAllPlayersHaveCharacters() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    gameOfSignedInUser.getPlayers().forEach(player -> assertNotNull(player.getCharacter()));
  }

  @Test
  public void whenStartGame_thenAllPlayersHaveHealthPointsAndPrestige() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    gameOfSignedInUser
        .getPlayers()
        .forEach(
            player -> {
              if (player.getRole() == Role.MANAGER) {
                assertEquals(5, player.getHealthPoints());
              } else {
                assertEquals(4, player.getHealthPoints());
              }
              assertEquals(player.getPrestige(), 0);
            });
  }

  @Test
  public void whenStartGame_thenAllPlayersHaveCards() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    gameOfSignedInUser
        .getPlayers()
        .forEach(
            player -> {
              if (player.getRole() == Role.MANAGER) {
                assertEquals(5, player.getCards().size());
              } else {
                assertEquals(4, player.getCards().size());
              }
            });
  }

  @Test
  public void whenStartGame_thenDeckIsNotEmpty() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    assertFalse(gameOfSignedInUser.getDeck().isEmpty());
  }

  @Test
  public void whenDrawCardFromDeck_thenSuccessful() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    getSignedInPlayer()
        .ifPresent(
            signedInPlayer -> {
              Optional<Card> maybeFirst = gameOfSignedInUser.drawFromDeck(signedInPlayer);
              Optional<Card> maybeSecond = gameOfSignedInUser.drawFromDeck(signedInPlayer);

              assertNotNull(maybeFirst);
              assertNotNull(maybeFirst);

              // It's not the same card - comparison through equals() & hashCode() (cardId)
              maybeFirst.ifPresent(
                  first -> maybeSecond.ifPresent(second -> assertNotEquals(first, second)));
            });
  }

  @Test
  public void whenDiscardCard_thenSuccessful() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    // Discard the first card of the signed-in player
    getSignedInPlayer()
        .ifPresent(
            currentPlayer ->
                gameOfSignedInUser.discardToPile(
                    currentPlayer.removeFromHand(currentPlayer.getCards().get(0)), currentPlayer));

    assertFalse(gameOfSignedInUser.getDiscardPile().isEmpty());
  }

  @Test
  public void whenGetPlayerOnTurn_thenSuccessful() {
    Optional<Player> maybeManager = getManagerOfStartedGame();
    ArrayList<Player> all = new ArrayList<>(gameOfSignedInUser.getPlayers());
    // Test that the manager is the one who is currently on turn
    maybeManager.ifPresent(
        manager ->
            viewModel
                .getPlayerOnTurn()
                .ifPresent(
                    player -> {
                      assertEquals(gameOfSignedInUser.getCurrentTurnIndex(), all.indexOf(manager));
                      assertEquals(manager.getUsername(), player.getUsername());
                    }));
  }

  @Test
  public void whenEndTurn_thenSuccessful() {
    Optional<Player> maybeManager = getManagerOfStartedGame();
    ArrayList<Player> all = new ArrayList<>(gameOfSignedInUser.getPlayers());

    viewModel.endTurn();

    // Test that the manager is no longer on turn
    maybeManager.ifPresent(
        manager ->
            viewModel
                .getPlayerOnTurn()
                .ifPresent(
                    player -> {
                      assertNotEquals(
                          gameOfSignedInUser.getCurrentTurnIndex(), all.indexOf(manager));
                      assertNotEquals(manager.getUsername(), player.getUsername());
                    }));
  }

  private Optional<Player> getManagerOfStartedGame() {
    List<Game> games = createTestGames();
    Mockito.when(mockService.getGames()).thenReturn(games);
    // Add two more players in order to be able to start
    gameOfSignedInUser.add(createTestPlayer("bruce", Type.PLAYER));
    gameOfSignedInUser.add(createTestPlayer("marvel", Type.PLAYER));

    viewModel.startGame();

    // Get the manager from the started game
    ArrayList<Player> all = new ArrayList<>(gameOfSignedInUser.getPlayers());
    return all.stream().filter(player -> player.getRole() == Role.MANAGER).findFirst();
  }

  private Game createTestGame() {
    Game game = new Game("Starbucks", 4, false, "", Status.READY, signedInUser.getUsername());
    game.add(createTestPlayer("john_doe", Type.HOST));
    game.add(createTestPlayer("amy", Type.PLAYER));

    return game;
  }

  private List<Game> createTestGames() {
    List<Game> games = new ArrayList<>();
    Game fullGame = new Game("Coffee", 4, true, "123", Status.READY, "ron");
    fullGame.add(createTestPlayer("ron", Type.HOST));
    fullGame.add(createTestPlayer("lea", Type.PLAYER));
    fullGame.add(createTestPlayer("eric", Type.PLAYER));
    fullGame.add(createTestPlayer("larry", Type.PLAYER));

    games.add(gameOfSignedInUser);
    games.add(fullGame);

    games.add(new Game("Java", 4, true, "123", Status.STARTED, "klara"));
    games.add(new Game("Campus Suite", 6, true, "secret", Status.PAUSED, "peter"));
    games.add(new Game("Ghost", 5, false, "", Status.READY, "simone"));

    return games;
  }

  private User createTestUser(String username) {
    // Assume this user is already in a game
    return new User("John Doe", username, "super secret", Collections.emptySet(), false, false);
  }

  private Set<Player> createTestPlayers() {
    Set<Player> players = new HashSet<>();
    players.add(createTestPlayer("john_doe", Type.HOST));
    players.add(createTestPlayer("amy", Type.PLAYER));
    players.add(createTestPlayer("jack", Type.PLAYER));
    return players;
  }

  /**
   * Be careful! There is a dependency between this method and getTestUser()! Both have to have the
   * same username, otherwise there is inconsistency.
   *
   * @param type The role which the player has
   * @return A test player object
   */
  private Player createTestPlayer(String username, Type type) {
    Player player = new Player();
    player.setUsername(username);
    player.setPrestige(0);
    player.setHealthPoints(4);
    player.setType(type);
    player.setCards(new ArrayList<>());

    return player;
  }

  public Optional<Player> getSignedInPlayer() {
    return gameOfSignedInUser.getPlayers().stream()
        .filter(player -> player.getUsername().equals(signedInUser.getUsername()))
        .findFirst();
  }
}
