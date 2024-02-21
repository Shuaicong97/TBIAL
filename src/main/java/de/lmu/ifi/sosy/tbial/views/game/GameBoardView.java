// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.views.game;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.BugCard;
import de.lmu.ifi.sosy.tbial.core.DiscardedCard;
import de.lmu.ifi.sosy.tbial.core.DrawnCard;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.cards.Role;
import de.lmu.ifi.sosy.tbial.core.chats.ChatMessage;
import de.lmu.ifi.sosy.tbial.core.game.Game;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.security.SecurityConfiguration;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;
import de.lmu.ifi.sosy.tbial.views.*;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.ChatView;
import de.lmu.ifi.sosy.tbial.views.MessageList;
import de.lmu.ifi.sosy.tbial.views.dashboard.DashboardLeft;
import de.lmu.ifi.sosy.tbial.views.dashboard.DashboardRight;
import de.lmu.ifi.sosy.tbial.views.dashboard.DashboardTop;
import de.lmu.ifi.sosy.tbial.views.dashboard.OwnDashboard;
import de.lmu.ifi.sosy.tbial.views.dialogs.RulesDialog;
import java.util.*;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("GameBoard")
@Route(value = Helpers.GAME_BOARD, layout = GameBoardLayout.class)
@PermitAll
@PreserveOnRefresh
@CssImport("./styles/gameBoard.css")
public class GameBoardView extends VerticalLayout {

  private static final long serialVersionUID = 6437333691282922374L;
  private Registration broadcasterRegistration;

  private final MessageList messageList = new MessageList();

  private Image deck;

  private OwnDashboard ownDashboard;
  private Button endTurn;
  private Button endDefense;
  private DashboardTop topDashboard;
  private DashboardTop topLeft;
  private DashboardTop topRight;

  private DashboardRight rightDashboard;
  private DashboardRight rightUp;
  private DashboardRight rightDown;

  private DashboardLeft leftDashboard;
  private DashboardLeft leftDown;
  private DashboardLeft leftUp;

  public GameBoardView(@Autowired GameViewModel gameViewModel) {
    gameViewModel
        .getCurrentPlayer()
        .ifPresent(
            currentPlayer ->
                gameViewModel
                    .getCurrentGame()
                    .ifPresentOrElse(
                        game -> showGameBoard(game, currentPlayer, gameViewModel),
                        () -> {
                          // Someone doesn't have a current game because they were
                          // kicked out or left the game
                          UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
                        }));

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void showGameBoard(Game game, Player currentPlayer, GameViewModel gameViewModel) {
    notifyManagerStarts(game);
    // Listen for UI updates
    attachListener(gameViewModel, this, game, currentPlayer);

    endTurn = new Button("End Turn");
    // endDefense Button only shows when the player is attacked
    endDefense = new Button("End Defense");
    endDefense.setEnabled(false);
    endDefense.addClickListener(
        event -> {
          Broadcaster.broadcast(new ViewData<>(currentPlayer, ViewData.Type.BUG_CARD_REMOVED));
          currentPlayer.setHealthPoints(
              currentPlayer.getHealthPoints() - currentPlayer.getNumOfBeingAttacked());
          if (currentPlayer.isDead()) {
            checkIfOnlyOnePlayerIsLeft(game);
          }
          Broadcaster.broadcast(new ViewData<>(currentPlayer, ViewData.Type.PLAYER_LOST_LIFE));
          writeAttackConsequenceMessages(game, currentPlayer);
        });

    // End turn should only be enabled and two cards drawn for the player whose turn currently is.
    // Once at the initial UI set-up.
    gameViewModel
        .getPlayerOnTurn()
        .ifPresent(
            player -> {
              boolean isCurrentPlayerOnTurn =
                  player.getUsername().equals(currentPlayer.getUsername());
              endTurn.setEnabled(isCurrentPlayerOnTurn);
              enableDashboardsForAttack(isCurrentPlayerOnTurn);
              if (isCurrentPlayerOnTurn) {
                // Draw two cards
                gameViewModel.drawCard(game, player);
                gameViewModel.drawCard(game, player);

                currentPlayer.setNumOfBugPlayedPerTurn(0);
              }
            });
    // And to be updated dynamically when players change turns.
    endTurn.addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  Player onTurn =
                                      new ArrayList<>(game.getPlayers())
                                          .get(game.getCurrentTurnIndex());

                                  endTurn.setEnabled(
                                      !game.isAttacked()
                                          && currentPlayer
                                              .getUsername()
                                              .equals(onTurn.getUsername()));
                                  enableDashboardsForAttack(
                                      currentPlayer.getUsername().equals(onTurn.getUsername()));

                                  if (!game.isAttacked())
                                    enableOwnCardHands(
                                        onTurn.getUsername().equals(currentPlayer.getUsername()));
                                })));
    HorizontalLayout buttons = new HorizontalLayout();

    Button leave = new Button("Leave Game");
    leave.addClickListener(
        e -> {
          // Leave the game and kill the player
          gameViewModel.leaveGame();
          currentPlayer.setHealthPoints(0);
          endTurnIfPlayerDead(gameViewModel, game);
          checkIfOnlyOnePlayerIsLeft(game);
          Broadcaster.broadcast(new ViewData<>(gameViewModel.getAllGames(), ViewData.Type.GAMES));
          Broadcaster.broadcast(new ViewData<>(currentPlayer, ViewData.Type.PLAYER_LOST_LIFE));

          Broadcaster.postMessage(
              new ChatMessage(
                  game.getName(), currentPlayer.getUsername() + " left the game.", game.getName()));

          // Notify who's turn it is.
          Broadcaster.postMessage(
              new ChatMessage(
                  game.getName(),
                  "It's your turn, "
                      + new ArrayList<>(game.getPlayers())
                          .get(game.getCurrentTurnIndex())
                          .getUsername()
                      + "!",
                  game.getName()));

          // Redirect to the games list
          leave.getUI().ifPresent(ui -> ui.navigate(Helpers.GAMES_LIST));
        });

    Button rules = new Button("See Rules");
    rules.addClickListener(e -> new RulesDialog().open());
    buttons.add(leave, rules, endTurn, endDefense);

    gameViewModel
        .getPlayerOnTurn()
        .ifPresent(player -> add(buildBoardAndChat(game, currentPlayer, gameViewModel), buttons));
  }

  private void discardSurplusCards() {
    Notification notification = new Notification();
    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

    Div text =
        new Div(
            new Text(
                "Too many cards! Please discard the cards so that their number is the same as your health points."));

    Button closeButton = new Button(new Icon("lumo", "cross"));
    closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
    closeButton.getElement().setAttribute("aria-label", "Close");
    closeButton.addClickListener(event -> notification.close());

    HorizontalLayout layout = new HorizontalLayout(text, closeButton);
    layout.setAlignItems(Alignment.CENTER);

    notification.add(layout);
    notification.open();
    notification.setDuration(2500);
    notification.setPosition(Notification.Position.MIDDLE);
  }

  private void checkIfOnlyOnePlayerIsLeft(Game game) {
    ArrayList<Player> players = new ArrayList<>();
    game.getPlayers()
        .forEach(
            player -> {
              if (!player.isDead()) {
                players.add(player);
              }
            });
    if (players.size() == 1) {
      Broadcaster.broadcast(new ViewData<>(players.get(0), ViewData.Type.POP_UP_WINNER));
    }
  }

  private void endTurnIfPlayerDead(GameViewModel gameViewModel, Game game) {
    gameViewModel
        .getPlayerOnTurn()
        .ifPresent(
            player -> {
              if (player.isDead()) {
                endTurn(gameViewModel, game);
              }
            });
  }

  private void endTurn(GameViewModel gameViewModel, Game game) {
    gameViewModel.endTurn(); // End the turn of the current player.
    gameViewModel
        .getPlayerOnTurn()
        .ifPresent(
            player -> {
              if (player.isDead()) {
                endTurn(gameViewModel, game);
              } else {
                // Player on turn draws two cards:
                gameViewModel.drawCard(game, player);
                gameViewModel.drawCard(game, player);

                // Notify who's turn it is.
                Broadcaster.postMessage(
                    new ChatMessage(
                        game.getName(),
                        "It's your turn, " + player.getUsername() + "!",
                        game.getName()));
              }
            });
  }

  private HorizontalLayout buildBoardAndChat(
      Game currentGame, Player currentPlayer, GameViewModel gameViewModel) {
    HorizontalLayout horizontalLayout = new HorizontalLayout();
    horizontalLayout.add(
        assignPlayersToDashboards(currentGame, currentPlayer, gameViewModel),
        new ChatView(currentGame.getName(), currentPlayer.getUsername(), messageList, "Game Chat"));
    return horizontalLayout;
  }

  private void attachListener(
      GameViewModel gameViewModel, VerticalLayout layout, Game game, Player currentPlayer) {
    layout.addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  switch (data.getType()) {
                                    case MESSAGE:
                                      onMsgReceived((ChatMessage) data.getPayload(), game);
                                      break;
                                    case PLAYER_LOST_LIFE:
                                      updateLives((Player) data.getPayload());
                                      break;
                                    case BUG_CARD_ADDED:
                                      game.setAttacked(true);
                                      changeTurnToVictim(
                                          (BugCard) data.getPayload(), currentPlayer);
                                      break;
                                    case BUG_CARD_REMOVED:
                                      game.setAttacked(false);
                                      changeTurnToPlayerOnTurn(currentPlayer, gameViewModel);
                                      break;
                                    case POP_UP_WINNER:
                                      openPopUpWinner((Player) data.getPayload());
                                      break;
                                    default:
                                  }
                                })));
  }

  private void changeTurnToVictim(BugCard payload, Player currentPlayer) {

    // Enable attackedPlayer's dashboard, disable attackPlayer's dashboard
    if (currentPlayer.getUsername().equals(payload.getReceiver().getUsername())) {
      enableOwnCardHands(true);
      endDefense.setEnabled(true);
    }
    if (currentPlayer.getUsername().equals(payload.getTrigger().getUsername())) {
      enableOwnCardHands(false);
      endTurn.setEnabled(false);
    }
  }

  private void changeTurnToPlayerOnTurn(Player currentPlayer, GameViewModel gameViewModel) {
    // Disable attackedPlayer's dashboard, enable attackPlayer's dashboard
    gameViewModel
        .getPlayerOnTurn()
        .ifPresent(
            player -> {
              enableOwnCardHands(player.getUsername().equals(currentPlayer.getUsername()));
              endTurn.setEnabled(player.getUsername().equals(currentPlayer.getUsername()));
              endDefense.setEnabled(false);
            });
  }

  private void writeAttackMessages(
      Game game, Player currentPlayer, Player player, DrawnCard drawnCard) {
    Broadcaster.postMessage(
        new ChatMessage(
            game.getName(),
            currentPlayer.getUsername()
                + " attacked "
                + player.getUsername()
                + " with "
                + drawnCard.getCard().getName()
                + ". "
                + player.getUsername()
                + " should draw a solution or a lame excuse card, or a life will be lost.",
            game.getName()));
  }

  private void writeAttackConsequenceMessages(Game game, Player player) {
    if (player.isDead()) {
      Broadcaster.postMessage(
          new ChatMessage(
              game.getName(),
              player.getUsername() + " is dead. Player was " + player.getRole().getName() + ".",
              game.getName()));
    } else if (player.getNumOfBeingAttacked() == 0) {
      Broadcaster.postMessage(
          new ChatMessage(
              game.getName(),
              player.getUsername()
                  + " blocked the attack. "
                  + player.getUsername()
                  + " has "
                  + player.getHealthPoints()
                  + " lives left.",
              game.getName()));
    } else {
      Broadcaster.postMessage(
          new ChatMessage(
              game.getName(),
              player.getUsername()
                  + " lost a life. "
                  + player.getUsername()
                  + " has "
                  + player.getHealthPoints()
                  + " lives left.",
              game.getName()));
    }
  }

  private void onMsgReceived(ChatMessage msg, Game game) {
    if (game.getName().equals(msg.getChatID())) updateChat(msg);
  }

  private HorizontalLayout assignPlayersToDashboards(
      Game game, Player currentPlayer, GameViewModel gameViewModel) {
    HorizontalLayout gameBoardLayout = new HorizontalLayout();

    List<Player> all = new ArrayList<>(game.getPlayers());

    // Personal dashboard
    ownDashboard = new OwnDashboard(currentPlayer, game);
    ownDashboard.setJustifyContentMode(JustifyContentMode.CENTER);

    // Left dashboard
    VerticalLayout leftVertical = new VerticalLayout();
    leftVertical.setJustifyContentMode(JustifyContentMode.CENTER);
    leftVertical.addClassNames("shiftLeftContainer");

    // Top dashboard
    HorizontalLayout topHorizontal = new HorizontalLayout();
    topHorizontal.setWidth("100%");
    topHorizontal.setJustifyContentMode(JustifyContentMode.CENTER);

    // Right dashboard
    VerticalLayout rightVertical = new VerticalLayout();
    rightVertical.setJustifyContentMode(JustifyContentMode.CENTER);
    rightVertical.addClassNames("shiftRightContainer");

    // Apart from the current player, others are distributed:
    switch (game.getPlayersCount()) {
      case 4:
        // One left
        leftDashboard = new DashboardLeft(getNext(all, currentPlayer, 1));
        DropTarget<DashboardLeft> dropTargetLeftDashboard = DropTarget.create(leftDashboard);
        dropTargetLeftDashboard.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                // consider 6 types of Bug cards. If so, the attacked player needs to respond with a
                // lame excuse or solution on his turn,
                // otherwise he/she will lose one life.
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = leftDashboard.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        leftVertical.add(leftDashboard);
        // One top
        topDashboard = new DashboardTop(getNext(all, currentPlayer, 2), game);
        DropTarget<DashboardTop> dropTargetTopDashboard = DropTarget.create(topDashboard);
        dropTargetTopDashboard.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();

                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = topDashboard.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        topHorizontal.add(topDashboard);
        // One right
        rightDashboard = new DashboardRight(getNext(all, currentPlayer, 3));
        DropTarget<DashboardRight> dropTargetRightDashboard = DropTarget.create(rightDashboard);
        dropTargetRightDashboard.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();

                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = rightDashboard.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        rightVertical.add(rightDashboard);
        break;
      case 5:
        // One left
        leftDashboard = new DashboardLeft(getNext(all, currentPlayer, 1));
        DropTarget<DashboardLeft> dropTargetLeftDashboard1 = DropTarget.create(leftDashboard);
        dropTargetLeftDashboard1.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();

                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = leftDashboard.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        leftVertical.add(leftDashboard);
        // Two top
        topLeft = new DashboardTop(getNext(all, currentPlayer, 2), game);
        DropTarget<DashboardTop> dropTargetTopLeft1 = DropTarget.create(topLeft);
        dropTargetTopLeft1.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = topLeft.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        topRight = new DashboardTop(getNext(all, currentPlayer, 3), game);
        DropTarget<DashboardTop> dropTargetTopRight1 = DropTarget.create(topRight);
        dropTargetTopRight1.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = topRight.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        topHorizontal.add(topLeft, topRight);
        // One right
        rightDashboard = new DashboardRight(getNext(all, currentPlayer, 4));
        DropTarget<DashboardRight> dropTargetRightDashboard1 = DropTarget.create(rightDashboard);
        dropTargetRightDashboard1.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = rightDashboard.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        rightVertical.add(rightDashboard);
        break;
      case 6:
        // Two left
        leftDown = new DashboardLeft(getNext(all, currentPlayer, 1));
        DropTarget<DashboardLeft> dropTargetLeftDown2 = DropTarget.create(leftDown);
        dropTargetLeftDown2.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = leftDown.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        leftUp = new DashboardLeft(getNext(all, currentPlayer, 2));
        DropTarget<DashboardLeft> dropTargetLeftUp2 = DropTarget.create(leftUp);
        dropTargetLeftUp2.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = leftUp.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        leftVertical.add(leftUp, leftDown);
        // Two top
        topLeft = new DashboardTop(getNext(all, currentPlayer, 3), game);
        DropTarget<DashboardTop> dropTargetTopLeft2 = DropTarget.create(topLeft);
        dropTargetTopLeft2.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = topLeft.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        topRight = new DashboardTop(getNext(all, currentPlayer, 4), game);
        DropTarget<DashboardTop> dropTargetTopRight2 = DropTarget.create(topRight);
        dropTargetTopRight2.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = topRight.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        topHorizontal.add(topLeft, topRight);
        // One right
        rightDashboard = new DashboardRight(getNext(all, currentPlayer, 5));
        DropTarget<DashboardRight> dropTargetRightDashboard2 = DropTarget.create(rightDashboard);
        dropTargetRightDashboard2.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = rightDashboard.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        rightVertical.add(rightDashboard);
        break;
      case 7:
        // Two left
        leftDown = new DashboardLeft(getNext(all, currentPlayer, 1));
        DropTarget<DashboardLeft> dropTargetLeftDown3 = DropTarget.create(leftDown);
        dropTargetLeftDown3.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = leftDown.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        leftUp = new DashboardLeft(getNext(all, currentPlayer, 2));
        DropTarget<DashboardLeft> dropTargetLeftUp3 = DropTarget.create(leftUp);
        dropTargetLeftUp3.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = leftUp.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        leftVertical.add(leftUp, leftDown);
        // Two top
        topLeft = new DashboardTop(getNext(all, currentPlayer, 3), game);
        DropTarget<DashboardTop> dropTargetTopLeft3 = DropTarget.create(topLeft);
        dropTargetTopLeft3.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = topLeft.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        topRight = new DashboardTop(getNext(all, currentPlayer, 4), game);
        DropTarget<DashboardTop> dropTargetTopRight3 = DropTarget.create(topRight);
        dropTargetTopRight3.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = topRight.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        topHorizontal.add(topLeft, topRight);
        // Two right
        rightUp = new DashboardRight(getNext(all, currentPlayer, 5));
        DropTarget<DashboardRight> dropTargetRightUp3 = DropTarget.create(rightUp);
        dropTargetRightUp3.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = rightUp.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        rightDown = new DashboardRight(getNext(all, currentPlayer, 6));
        DropTarget<DashboardRight> dropTargetRightDown3 = DropTarget.create(rightDown);
        dropTargetRightDown3.addDropListener(
            event -> {
              if (event.getDragData().isPresent()) {
                DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
                if (currentPlayer.getNumOfBugPlayedPerTurn() < 1
                    && (drawnCard.getCard().getName().equals("NullPointer")
                        || drawnCard.getCard().getName().equals("OffByOne")
                        || drawnCard.getCard().getName().equals("ClassNotFound")
                        || drawnCard.getCard().getName().equals("SystemHangs")
                        || drawnCard.getCard().getName().equals("CoreDump")
                        || drawnCard.getCard().getName().equals("CustomerHatesUI"))) {
                  Player attackedPlayer = rightDown.getPlayer();
                  broadcastAttackedPlayer(attackedPlayer, game, currentPlayer, drawnCard);
                  currentPlayer.setNumOfBugPlayedPerTurn(
                      currentPlayer.getNumOfBugPlayedPerTurn() + 1);
                }
              }
            });
        rightVertical.add(rightUp, rightDown);
        break;
      default:
    }

    // Middle part of the screen
    VerticalLayout middleVertical = new VerticalLayout();

    middleVertical.add(
        topHorizontal, buildDeckAndHeap(game, currentPlayer, gameViewModel), ownDashboard);
    middleVertical.addClassNames("shiftMidContainer");

    gameBoardLayout.add(leftVertical, middleVertical, rightVertical);
    gameBoardLayout.addClassNames("backgroundImage");

    endTurn.addClickListener(
        e -> {
          if (ownDashboard
                  .getPlayableCardsHorizontalComponent()
                  .getCardComponent()
                  .getComponentCount()
              > currentPlayer.getHealthPoints()) discardSurplusCards();
          else {
            currentPlayer.setNumOfBugPlayedPerTurn(0);
            endTurn(gameViewModel, game);
          }
        });

    return gameBoardLayout;
  }

  private HorizontalLayout buildDeckAndHeap(
      Game game, Player currentPlayer, GameViewModel gameViewModel) {
    HorizontalLayout layout = new HorizontalLayout();
    HorizontalLayout discard = new HorizontalLayout();

    // Create and attach listener to the deck.
    deck = ImageBuilder.getCardImage("BackSide", Helpers.CARD_HEIGHT, Helpers.CARD_WIDTH);

    // Enable drawing and playing cards only if the user is on turn
    gameViewModel
        .getPlayerOnTurn()
        .ifPresent(
            player -> {
              deck.setEnabled(player.getUsername().equals(currentPlayer.getUsername()));
              enableOwnCardHands(player.getUsername().equals(currentPlayer.getUsername()));
            });

    // Visualize the discard pile and make it drop target
    discard.addClassName("discardPile");
    Image discardPileImage =
        ImageBuilder.getCardImage("DiscardPile", Helpers.CARD_HEIGHT, Helpers.CARD_WIDTH);
    discardPileImage.addClassName("emptyDiscardPile");
    discard.add(discardPileImage);
    DropTarget<HorizontalLayout> dropTargetDiscardPile = DropTarget.create(discard);
    dropTargetDiscardPile.addDropListener(
        event -> {
          if (event.getDragData().isPresent()) {
            DrawnCard drawnCard = (DrawnCard) event.getDragData().get();
            game.discardToPile(
                currentPlayer.removeFromHand(drawnCard.getCard()), drawnCard.getPlayer());
          }
        });

    discard.addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  if (data.getType() == ViewData.Type.DISCARD_PILE) {
                                    DiscardedCard discardedCard = (DiscardedCard) data.getPayload();
                                    Image img = discardedCard.getCard().getImage();
                                    img.addClassNames();
                                    discard.add();
                                  }
                                })));

    layout.setWidth("100%");
    layout.setJustifyContentMode(JustifyContentMode.CENTER);
    layout.add(deck, discard);

    return layout;
  }

  private void enableOwnCardHands(boolean isOnTurn) {
    ownDashboard.getPlayableCardsHorizontalComponent().enableOwnDashboardOnTurn(isOnTurn);
  }

  private void enableDashboardsForAttack(boolean isCurrentPlayerOnTurn) {
    if (topDashboard != null) {
      topDashboard.setEnabled(isCurrentPlayerOnTurn);
    }
    if (topRight != null) {
      topRight.setEnabled(isCurrentPlayerOnTurn);
    }
    if (topLeft != null) {
      topLeft.setEnabled(isCurrentPlayerOnTurn);
    }
    if (leftDashboard != null) {
      leftDashboard.setEnabled(isCurrentPlayerOnTurn);
    }
    if (leftUp != null) {
      leftUp.setEnabled(isCurrentPlayerOnTurn);
    }
    if (leftDown != null) {
      leftDown.setEnabled(isCurrentPlayerOnTurn);
    }
    if (rightDashboard != null) {
      rightDashboard.setEnabled(isCurrentPlayerOnTurn);
    }
    if (rightUp != null) {
      rightUp.setEnabled(isCurrentPlayerOnTurn);
    }
    if (rightDown != null) {
      rightDown.setEnabled(isCurrentPlayerOnTurn);
    }
  }

  private void broadcastAttackedPlayer(
      Player attackedPlayer, Game game, Player currentPlayer, DrawnCard drawnCard) {
    if (!attackedPlayer.isDead()) {
      attackedPlayer.setNumOfBeingAttacked(1);
      writeAttackMessages(game, currentPlayer, attackedPlayer, drawnCard);
      Broadcaster.broadcast(
          new ViewData<>(
              new BugCard(drawnCard.getCard(), currentPlayer, attackedPlayer),
              ViewData.Type.BUG_CARD_ADDED));
      game.discardToPile(currentPlayer.removeFromHand(drawnCard.getCard()), drawnCard.getPlayer());
    }
  }

  // updates the brains of all existing dashboards
  private void updateLives(Player attackedPlayer) {
    if (ownDashboard.getPlayer() == attackedPlayer) {
      ownDashboard.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
      ownDashboard.updateMentalHealth(attackedPlayer);
    }
    if (topDashboard != null) {
      if (topDashboard.getPlayer() == attackedPlayer) {
        topDashboard.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        topDashboard.updateMentalHealth(attackedPlayer);
      }
    }
    if (topLeft != null) {
      if (topLeft.getPlayer() == attackedPlayer) {
        topLeft.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        topLeft.updateMentalHealth(attackedPlayer);
      }
    }
    if (topRight != null) {
      if (topRight.getPlayer() == attackedPlayer) {
        topRight.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        topRight.updateMentalHealth(attackedPlayer);
      }
    }
    if (leftDashboard != null) {
      if (leftDashboard.getPlayer() == attackedPlayer) {
        leftDashboard.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        leftDashboard.updateMentalHealth(attackedPlayer);
      }
    }
    if (leftDown != null) {
      if (leftDown.getPlayer() == attackedPlayer) {
        leftDown.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        leftDown.updateMentalHealth(attackedPlayer);
      }
    }
    if (leftUp != null) {
      if (leftUp.getPlayer() == attackedPlayer) {
        leftUp.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        leftUp.updateMentalHealth(attackedPlayer);
      }
    }
    if (rightDashboard != null) {
      if (rightDashboard.getPlayer() == attackedPlayer) {
        rightDashboard.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        rightDashboard.updateMentalHealth(attackedPlayer);
      }
    }
    if (rightUp != null) {
      if (rightUp.getPlayer() == attackedPlayer) {
        rightUp.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        rightUp.updateMentalHealth(attackedPlayer);
      }
    }
    if (rightDown != null) {
      if (rightDown.getPlayer() == attackedPlayer) {
        rightDown.getPlayer().setHealthPoints(attackedPlayer.getHealthPoints());
        rightDown.updateMentalHealth(attackedPlayer);
      }
    }
  }

  private void openPopUpWinner(Player player) {
    Notification notification = new Notification();
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

    Div text = new Div(new Text("Congrats " + player.getUsername() + ", you won!"));

    Button closeButton = new Button(new Icon("lumo", "cross"));
    closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
    closeButton.getElement().setAttribute("aria-label", "Close");
    closeButton.addClickListener(event -> notification.close());

    HorizontalLayout layout = new HorizontalLayout(text, closeButton);
    layout.setAlignItems(Alignment.CENTER);

    notification.add(layout);
    notification.open();
    notification.setPosition(Notification.Position.MIDDLE);
  }

  private void notifyManagerStarts(Game game) {
    ArrayList<Player> all = new ArrayList<>(game.getPlayers());
    // Find the manager because it's their first turn
    Optional<Player> maybeManager =
        all.stream().filter(player -> player.getRole() == Role.MANAGER).findFirst();
    maybeManager.ifPresent(
        manager -> {
          // Not using Broadcaster here because at this point the UI is still loading
          ChatMessage msg =
              new ChatMessage(
                  game.getName(), manager.getUsername() + " plays first.", game.getName());
          messageList.add(
              new Paragraph(msg.getDate() + " " + msg.getSender() + ": " + msg.getMessage()));
        });
  }

  /**
   * Returns the next player from the perspective of the current player. E.g. the function returns
   * the second next or third next player depending on the index of the current. Indexes never run
   * out of bounds because of the modulo operator.
   *
   * @param all All players in a game
   * @param current The current player depending on whom we return the next
   * @param away Home many steps away from the current to move
   * @return The next player.
   */
  public static Player getNext(List<Player> all, Player current, int away) {
    return all.get((all.indexOf(current) + away) % all.size());
  }

  private void updateChat(ChatMessage msg) {
    messageList.add(new Paragraph(msg.getDate() + " " + msg.getSender() + ": " + msg.getMessage()));
  }
}
