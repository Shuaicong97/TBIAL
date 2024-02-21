package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.core.player.Type;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.UserViewModel;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("List of players")
@Route(value = Helpers.PLAYERS_LIST, layout = MainLayout.class)
@PermitAll
public class PlayersListView extends VerticalLayout {

  private static final long serialVersionUID = -4311095102732666410L;

  private Registration broadcasterRegistration;

  public PlayersListView(
      @Autowired GameViewModel gameViewModel, @Autowired UserViewModel userViewModel) {

    // initializing lists
    List<User> users = userViewModel.getOnlineUsers();

    // grid with online users:
    Grid<User> grid = new Grid<>(User.class, false);
    grid.setSelectionMode(Grid.SelectionMode.SINGLE);
    // Show all currently connected users when the view first loads
    grid.setItems(users);
    grid.addColumn(User::getUsername).setHeader("Username");
    grid.addColumn(User::getTypes).setHeader("Role");
    grid.addComponentColumn(
        user -> {
          Button inviteButton = new Button("Send Invitation");
          inviteButton.setVisible(false);
          showPossibleInvitations(gameViewModel, user, inviteButton);
          inviteButton.addClickListener(
              event -> {
                userViewModel.setInviteeUsername(user.getUsername());
                userViewModel.setInviterUsername(gameViewModel.getCurrentUser().getUsername());
                Broadcaster.broadcast(
                    new ViewData<>(
                        userViewModel.getOnlineUsers(), ViewData.Type.NOTIFICATION_INVITED));
              });
          return inviteButton;
        });

    // adding elements to layout
    add(new H4("List of currently online users: "));
    setMargin(true);
    add(grid);

    // Register callbacks to update the view dynamically
    addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  if (data.getType() == ViewData.Type.USERS) {
                                    grid.setItems(userViewModel.getOnlineUsers());
                                  }
                                })));

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void showPossibleInvitations(GameViewModel gameViewModel, User user, Button button) {
    User loginUser = gameViewModel.getCurrentUser();
    gameViewModel
        .getCurrentGame()
        .ifPresent(
            game -> {
              Set<Player> players = game.getPlayers();
              Optional<Player> self =
                  players.stream()
                      .filter(player -> loginUser.getUsername().equals(player.getUsername()))
                      .findFirst();
              if (self.isPresent()) {
                Player player = self.get();
                button.setVisible(player.getType() == Type.HOST && user.isAvailable());
              }
            });
  }
}
