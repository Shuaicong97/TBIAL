package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.chats.ChatMessage;
import de.lmu.ifi.sosy.tbial.core.game.Game;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.core.game.Status;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.core.player.Type;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.security.SecurityConfiguration;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.views.dialogs.KickDialog;
import de.lmu.ifi.sosy.tbial.views.dialogs.RulesDialog;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Lobby")
@Route(value = Helpers.LOBBY, layout = MainLayout.class)
@PermitAll
@CssImport(value = "./styles/playersInLobby.css")
public class LobbyView extends HorizontalLayout {

  private static final long serialVersionUID = -1306275372656114391L;
  private Registration broadcasterRegistration;
  private final VerticalLayout leftLayout = new VerticalLayout();

  private final MessageList messageList = new MessageList();

  public LobbyView(@Autowired GameViewModel gameViewModel) {
    add(createLeftPanel(gameViewModel));

    if (gameViewModel.getCurrentGame().isPresent()) {
      // Listen for UI updates
      attachListener(this, gameViewModel);
      if (gameViewModel.getCurrentPlayer().isPresent()) {
        add(
            new ChatView(
                gameViewModel.getCurrentGame().get().getName(),
                gameViewModel.getCurrentPlayer().get().getUsername(),
                messageList,
                "Lobby Chat"));
      }
    } else {
      // User tries to enter the lobby without a game
      UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
    }

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void attachListener(HorizontalLayout pageLayout, GameViewModel gameViewModel) {
    pageLayout.addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () ->
                                    gameViewModel
                                        .getCurrentGame()
                                        .ifPresentOrElse(
                                            currentGame -> {
                                              switch (data.getType()) {
                                                case GAMES:
                                                  onGamesChanged(
                                                      currentGame, gameViewModel, pageLayout);
                                                  break;
                                                case MESSAGE:
                                                  onMessagesChanged(
                                                      currentGame, (ChatMessage) data.getPayload());
                                                  break;
                                                default:
                                              }
                                            },
                                            () -> {
                                              // Someone doesn't have a current game because they
                                              // were
                                              // kicked out or left the game
                                              UI.getCurrent()
                                                  .getPage()
                                                  .setLocation(SecurityConfiguration.LOGOUT_URL);
                                            }))));
  }

  private void onGamesChanged(
      Game currentGame, GameViewModel gameViewModel, HorizontalLayout pageLayout) {
    // If the current game has started, navigate all users to the
    // game board
    if (currentGame.getStatus() == Status.STARTED) {
      UI.getCurrent().getPage().setLocation(Helpers.GAME_BOARD);
    } else {
      // Update the lobby (someone joined or left). Host may have left - re-render the UI component
      // to enable/disable Start button
      pageLayout.replace(leftLayout, createLeftPanel(gameViewModel));
    }
  }

  private void onMessagesChanged(Game currentGame, ChatMessage msg) {
    if (Objects.equals(currentGame.getName(), msg.getChatID())) {
      updateChat(msg);
    }
  }

  private void toggleStartButton(GameViewModel gameViewModel, Button start) {
    gameViewModel
        .getCurrentGame()
        .flatMap(
            game ->
                game.getPlayers().stream()
                    .filter(player -> player.getType() == Type.HOST)
                    .findFirst())
        .ifPresent(
            host ->
                start.setEnabled(
                    host.getUsername().equals(gameViewModel.getCurrentUser().getUsername())));
  }

  private Component createLeftPanel(GameViewModel gameViewModel) {
    leftLayout.removeAll();

    gameViewModel
        .getCurrentGame()
        .ifPresentOrElse(
            game ->
                leftLayout.add(
                    createGameInfoComponent(game),
                    createPlayersComponent(game.getPlayers(), gameViewModel),
                    createButtonsComponent(gameViewModel)),
            () -> UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL));
    return leftLayout;
  }

  private Component createButtonsComponent(GameViewModel gameViewModel) {
    HorizontalLayout buttonLayout = new HorizontalLayout();

    Button leave = new Button("Leave");
    leave.addClickListener(
        e -> {
          gameViewModel.leaveGame();
          Broadcaster.broadcast(new ViewData<>(gameViewModel.getAllGames(), ViewData.Type.GAMES));
          leave.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY)); // To cause a page refresh.
        });
    buttonLayout.add(leave);

    Button rules = new Button("See Rules", e -> new RulesDialog().open());
    buttonLayout.add(rules);

    Button start = new Button("Start");
    toggleStartButton(gameViewModel, start);
    start.addClickListener(e -> gameViewModel.startGame());
    buttonLayout.add(start);

    Button invite = new Button("Invite");
    invite.setEnabled(isInviteVisible(gameViewModel));

    invite.addClickListener(
        buttonClickEvent -> invite.getUI().ifPresent(ui -> ui.navigate(Helpers.PLAYERS_LIST)));
    buttonLayout.add(invite);

    return buttonLayout;
  }

  private boolean isInviteVisible(GameViewModel gameViewModel) {
    AtomicBoolean isInviteVisible = new AtomicBoolean(false);
    gameViewModel
        .getCurrentGame()
        .ifPresentOrElse(
            game ->
                isInviteVisible.set(
                    game.getPlayers().stream()
                        .anyMatch(
                            player ->
                                player.getType() == Type.HOST
                                    && player
                                        .getUsername()
                                        .equals(gameViewModel.getCurrentUser().getUsername()))),
            () -> isInviteVisible.set(false));

    return isInviteVisible.get();
  }

  private Component createGameInfoComponent(Game game) {
    HorizontalLayout gameInfoLayout = new HorizontalLayout();

    gameInfoLayout.add(new Div(new H4("Game")));
    gameInfoLayout.getStyle().set("align-items", "center").set("display", "flex");

    String gameName = game.getName();
    gameInfoLayout.add(new Div(new H4(gameName)));

    if (game.isLocked()) {
      gameInfoLayout.add((new Icon(VaadinIcon.LOCK)));
    } else {
      gameInfoLayout.add(new Icon(VaadinIcon.UNLOCK));
    }
    return gameInfoLayout;
  }

  public HorizontalLayout createPlayersComponent(Set<Player> players, GameViewModel gameViewModel) {
    HorizontalLayout layout = new HorizontalLayout();

    int currentPlayerSize = players.size();
    Optional<Game> currentGame = gameViewModel.getCurrentGame();
    int maxPlayers = 0;
    if (currentGame.isPresent()) {
      Game game = currentGame.get();
      maxPlayers = game.getMaxPlayers();
    }

    User loginUser = gameViewModel.getCurrentUser();
    boolean isInPlayers = false;

    // In the Set<User>, we have only two options: HOST or PLAYER
    for (Player player : players) {
      Type playerType = player.getType();
      Div userIconDiv;
      String username = player.getUsername();
      Div usernameDiv;

      if (playerType == Type.HOST) {
        userIconDiv = new Div(new Icon(VaadinIcon.USER_STAR));
      } else {
        userIconDiv = new Div(new Icon(VaadinIcon.USER));
        for (Player hostPlayer : players) {
          if (hostPlayer.getType() == Type.HOST
              && hostPlayer.getUsername().equals(loginUser.getUsername())) {
            userIconDiv.addClickListener(e -> new KickDialog(gameViewModel, username).open());
          }
        }
      }

      userIconDiv
          .getStyle()
          .set("border", "1px solid black")
          .set("align-items", "center")
          .set("display", "flex")
          .set("padding", "5px");

      // Set special style for login-User
      if (Objects.equals(player.getUsername(), loginUser.getUsername())) {
        userIconDiv.getStyle().set("background", "#d9d9d9");
        isInPlayers = true;
      }

      VerticalLayout playerLayout = new VerticalLayout();
      usernameDiv = new Div(new Text(username));
      playerLayout.add(userIconDiv);
      playerLayout.add(usernameDiv);
      playerLayout.setHorizontalComponentAlignment(Alignment.CENTER, userIconDiv, usernameDiv);
      layout.add(playerLayout);
    }

    if (!isInPlayers && UI.getCurrent().getUI().isPresent()) {
      UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
    }

    // Set left places as Available (Question Icon)
    for (int i = currentPlayerSize; i < maxPlayers; i++) {
      Div questionDiv = new Div(new Icon(VaadinIcon.QUESTION));
      questionDiv
          .getStyle()
          .set("border", "1px solid black")
          .set("align-items", "center")
          .set("display", "flex")
          .set("padding", "5px");

      VerticalLayout playerLayout = new VerticalLayout();
      playerLayout.add(questionDiv);
      layout.add(playerLayout);
    }
    return layout;
  }

  private void updateChat(ChatMessage msg) {
    TextArea chatMessage = new TextArea();
    chatMessage.setWidth("520px");
    chatMessage.setValue(msg.getDate() + " " + msg.getSender() + ": " + msg.getMessage());
    messageList.add(chatMessage);
  }
}
