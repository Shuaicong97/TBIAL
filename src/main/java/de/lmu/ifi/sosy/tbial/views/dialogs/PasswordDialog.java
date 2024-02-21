package de.lmu.ifi.sosy.tbial.views.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import java.util.Collections;

public class PasswordDialog extends Dialog {

  public PasswordDialog(GameViewModel gameViewModel, String password) {
    H2 headline = new H2("Enter password");
    getElement().setAttribute("aria-label", "Enter password");

    headline
        .getStyle()
        .set("margin", "var(--lumo-space-m) 0 0 0")
        .set("font-size", "1.5em")
        .set("font-weight", "bold");

    TextField passwordField = new TextField("Password of game");
    Div errorMessage = new Div(new Text("Password is wrong!"));
    errorMessage.addClassNames("error");
    errorMessage.setVisible(false);

    Button joinButton = new Button("Join");
    joinButton.addClickListener(
        event -> {
          if (passwordField.getValue().equals(password)) {
            errorMessage.setVisible(false);
            gameViewModel
                .getCurrentGame()
                .ifPresent(
                    game -> {
                      gameViewModel.addPlayerToGame(
                          game, gameViewModel.getCurrentUser().getUsername());
                      joinButton.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY));
                      Broadcaster.broadcast(
                          new ViewData<>(gameViewModel.getAllGames(), ViewData.Type.GAMES));
                      Broadcaster.broadcast(
                          new ViewData<>(
                              Collections.emptyList(), ViewData.Type.NOTIFICATION_JOINED));
                      close();
                    });
          } else {
            errorMessage.setVisible(true);
          }
        });

    joinButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    VerticalLayout dialogLayout =
        new VerticalLayout(headline, passwordField, errorMessage, joinButton);
    dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
    dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");

    add(dialogLayout);
  }
}
