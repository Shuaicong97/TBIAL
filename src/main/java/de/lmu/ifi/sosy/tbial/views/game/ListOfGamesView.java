package de.lmu.ifi.sosy.tbial.views.game;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.chats.ChatMessage;
import de.lmu.ifi.sosy.tbial.core.game.Game;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.core.player.Type;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.ChatView;
import de.lmu.ifi.sosy.tbial.views.MainLayout;
import de.lmu.ifi.sosy.tbial.views.MessageList;
import java.util.*;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("List of games")
@Route(value = Helpers.GAMES_LIST, layout = MainLayout.class)
@PermitAll
@CssImport(value = "./styles/gridGameList.css", themeFor = "vaadin-grid")
public class ListOfGamesView extends HorizontalLayout {

  private static final long serialVersionUID = -4328017202732666410L;
  private boolean filtersAreVisible = false;
  private Registration broadcasterRegistration;

  private final MessageList messageList = new MessageList();

  public ListOfGamesView(@Autowired GameViewModel gameViewModel) {

    // initializing lists
    List<Game> games = gameViewModel.getAllGames();

    VerticalLayout layout = new VerticalLayout();

    // Games and Players Button
    HorizontalLayout menuButtons = new HorizontalLayout();
    menuButtons.add(new Button("Games"));
    menuButtons.add(new Button("Players"));

    // grid with games:
    Grid<Game> grid = new Grid<>();
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    // Show all games when the view first loads
    grid.setItems(games);
    grid.addColumn(Game::getStatus).setHeader("Status");
    grid.addColumn(Game::getName).setHeader("Name");
    grid.addColumn(Game::getCapacity).setHeader("Players");
    grid.addColumn(Game::getLocked).setHeader("Accessibility");

    // Highlight own games
    User loginUser = gameViewModel.getCurrentUser();
    grid.setClassNameGenerator(
        game -> {
          boolean isMarked = false;
          for (Player player : game.getPlayers()) {
            if (player.getType() == Type.HOST
                && player.getUsername().equals(loginUser.getUsername())) {
              isMarked = true;
              break;
            }
          }
          return isMarked ? "line-highlight" : null;
        });

    // Filter buttons:
    VerticalLayout filterOptions = new VerticalLayout();
    filterOptions.setVisible(filtersAreVisible);

    // text search
    HorizontalLayout filterSearchByName = new HorizontalLayout();
    TextField gameName = new TextField("Name of the game");
    filterSearchByName.add(gameName);
    filterOptions.add(filterSearchByName);

    // filter checkboxes password
    RadioButtonGroup<String> checkboxGroupPassword = new RadioButtonGroup<>();
    checkboxGroupPassword.setLabel("Locked");
    checkboxGroupPassword.setItems("yes", "no");
    filterOptions.add(checkboxGroupPassword);

    // filter checkboxes full or not full
    RadioButtonGroup<String> checkboxGroupCapacity = new RadioButtonGroup<>();
    checkboxGroupCapacity.setLabel("Free places");
    checkboxGroupCapacity.setItems("yes", "no");
    filterOptions.add(checkboxGroupCapacity);

    RadioButtonGroup<String> checkboxMaxPlayers = new RadioButtonGroup<>();
    checkboxMaxPlayers.setLabel("Max Players");
    checkboxMaxPlayers.setItems("4", "5", "6", "7");
    filterOptions.add(checkboxMaxPlayers);

    RadioButtonGroup<String> checkboxStatus = new RadioButtonGroup<>();
    checkboxStatus.setLabel("Status");
    checkboxStatus.setItems("READY", "STARTED", "PAUSED");
    filterOptions.add(checkboxStatus);

    // submit button for filters
    Button filterSubmitButton = new Button("Submit");
    filterSubmitButton.addClickListener(
        event ->
            grid.setItems(
                gameViewModel.filter(
                    games,
                    gameName.getValue(),
                    checkboxGroupPassword.getValue(),
                    checkboxGroupCapacity.getValue(),
                    checkboxMaxPlayers.getValue(),
                    checkboxStatus.getValue())));
    filterOptions.add(filterSubmitButton);

    // New Game, Filters, Join Buttons
    HorizontalLayout buttons = new HorizontalLayout();
    buttons.add(new Button("New Game"));
    Button filterButton = new Button("Filters");
    filterButton.addClickListener(
        event -> {
          filtersAreVisible = !filtersAreVisible;
          filterOptions.setVisible(filtersAreVisible);
        });
    buttons.add(filterButton);

    Button join = new Button("Join");
    join.addClickListener(
        e -> {
          Optional<Game> selectedGame = grid.getSelectedItems().stream().findFirst();

          selectedGame.ifPresent(
              game -> {
                // Do nothing if currentUser is already in a game
                for (Game singleGame : gameViewModel.getAllGames()) {
                  if (singleGame.contains(gameViewModel.getCurrentUser().getUsername())
                      && !game.getName().equals(singleGame.getName())) {
                    Notification.show(
                        "You're already in a game and cannot join a new one.",
                        5000,
                        Notification.Position.BOTTOM_STRETCH);
                    join.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY));
                    return;
                  }
                }
                gameViewModel.joinGame(game.getName());

                Optional<Player> userFound =
                    game.getPlayers().stream()
                        .filter(
                            player ->
                                player
                                    .getUsername()
                                    .equals(gameViewModel.getCurrentUser().getUsername()))
                        .findFirst();
                if (userFound.isPresent() || (!game.isLocked() && game.canBeJoined())) {
                  join.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY));
                }
              });
        });
    buttons.add(join);

    // adding elements to layout
    layout.add(new H4("List of Games"));
    layout.add(menuButtons);
    layout.add(filterOptions);
    layout.add(grid);
    layout.add(buttons);

    setMargin(true);
    add(layout);

    add(
        new ChatView(
            "GeneralChat",
            gameViewModel.getCurrentUser().getUsername(),
            messageList,
            "General Chat"));

    // Register callbacks to update the view dynamically
    addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  if (data.getType() == ViewData.Type.GAMES) {
                                    grid.setItems((List<Game>) data.getPayload());
                                  } else if (data.getType() == ViewData.Type.MESSAGE) {
                                    onMessagesChanged((ChatMessage) data.getPayload());
                                  }
                                })));

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void onMessagesChanged(ChatMessage msg) {
    if (Objects.equals("GeneralChat", msg.getChatID())) {
      updateChat(msg);
    }
  }

  private void updateChat(ChatMessage msg) {
    TextArea chatMessage = new TextArea();
    chatMessage.setWidth("520px");
    chatMessage.setValue(msg.getDate() + " " + msg.getSender() + ": " + msg.getMessage());
    messageList.add(chatMessage);
  }
}
