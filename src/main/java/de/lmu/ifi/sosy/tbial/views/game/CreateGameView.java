package de.lmu.ifi.sosy.tbial.views.game;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.chats.ChatMessage;
import de.lmu.ifi.sosy.tbial.core.game.Game;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.core.game.Status;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.ChatView;
import de.lmu.ifi.sosy.tbial.views.MainLayout;
import de.lmu.ifi.sosy.tbial.views.MessageList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Create a new game")
@Route(value = Helpers.CREATE_GAME, layout = MainLayout.class)
@PermitAll
public class CreateGameView extends HorizontalLayout {

  private static final long serialVersionUID = -4328018602732666410L;
  private Registration broadcasterRegistration;

  private final MessageList messageList = new MessageList();

  public CreateGameView(@Autowired GameViewModel gameViewModel) {
    VerticalLayout layout = new VerticalLayout();
    layout.add(new H4("Game Settings"));
    CreateGameInput gameInput = new CreateGameInput();

    layout.add(createNameComponent(gameInput));
    layout.add(createNumberOfPlayersComponent(gameInput));

    HorizontalLayout passwordComponent = createPasswordComponent(gameInput);
    layout.add(createTypeComponent(passwordComponent, gameInput));

    layout.add(passwordComponent);

    layout.add(createButtonsComponent(gameViewModel, gameInput));

    setMargin(true);

    add(layout);

    addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  if (data.getType() == ViewData.Type.MESSAGE) {
                                    onMessagesChanged((ChatMessage) data.getPayload());
                                  }
                                })));

    add(
        new ChatView(
            "GeneralChat",
            gameViewModel.getCurrentUser().getUsername(),
            messageList,
            "General Chat"));

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void makePrimary(Button button, List<Button> others) {
    button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    others.forEach(
        btn -> {
          if (!button.getText().equals(btn.getText())) {
            btn.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
          }
        });
  }

  private Component createNumberOfPlayersComponent(CreateGameInput gameInput) {
    HorizontalLayout layout = new HorizontalLayout();
    layout.add(new Paragraph("Max Players: "));

    List<Button> numberOfPlayers = new ArrayList<>();
    numberOfPlayers.add(new Button(String.valueOf(4)));
    numberOfPlayers.add(new Button(String.valueOf(5)));
    numberOfPlayers.add(new Button(String.valueOf(6)));
    numberOfPlayers.add(new Button(String.valueOf(7)));

    numberOfPlayers.forEach(
        btn ->
            btn.addClickListener(
                click -> {
                  gameInput.setMaxPlayers(Integer.parseInt(btn.getText()));
                  makePrimary(btn, numberOfPlayers);
                }));

    numberOfPlayers.forEach(layout::add);

    // Per default max players are set to 4
    Optional<Button> fourPlayersButton =
        numberOfPlayers.stream().filter(btn -> btn.getText().equals("4")).findFirst();
    fourPlayersButton.ifPresent(btn -> makePrimary(btn, numberOfPlayers));
    gameInput.setMaxPlayers(4);

    return layout;
  }

  private Component createNameComponent(CreateGameInput gameInput) {
    HorizontalLayout layout = new HorizontalLayout();
    layout.add(new Paragraph("Name: "));
    TextField name = new TextField();
    name.addValueChangeListener(e -> gameInput.setName(name.getValue()));
    layout.add(name);
    return layout;
  }

  private HorizontalLayout createPasswordComponent(CreateGameInput gameInput) {
    HorizontalLayout layout = new HorizontalLayout();
    layout.add(new Paragraph("Password: "));
    PasswordField password = new PasswordField();
    password.addValueChangeListener(e -> gameInput.setPassword(password.getValue()));
    layout.add(password);
    layout.setVisible(false);
    return layout;
  }

  private Component createTypeComponent(
      HorizontalLayout passwordComponent, CreateGameInput gameInput) {
    HorizontalLayout layout = new HorizontalLayout();
    layout.add(new Paragraph("Type: "));

    Button unlock = new Button(new Icon(VaadinIcon.UNLOCK));
    Button lock = new Button(new Icon(VaadinIcon.LOCK));

    unlock.addClickListener(
        click -> {
          gameInput.setLocked(false);
          passwordComponent.setVisible(false);
          unlock.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
          lock.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

    lock.addClickListener(
        click -> {
          gameInput.setLocked(true);
          passwordComponent.setVisible(true);
          lock.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
          unlock.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        });

    // Make unlocked the default value.
    unlock.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    gameInput.setLocked(false);

    layout.add(unlock, lock);
    return layout;
  }

  private Component createButtonsComponent(GameViewModel gameViewModel, CreateGameInput gameInput) {
    HorizontalLayout layout = new HorizontalLayout();
    Button cancel = new Button("Cancel");
    cancel.addClickListener(
        clickEvent -> cancel.getUI().ifPresent(ui -> ui.navigate(Helpers.HOME)));

    Button create = new Button("Create");
    create.addClickListener(
        click -> {
          if (gameViewModel.isInputValid(
              gameInput.getName(), gameInput.isLocked(), gameInput.getPassword())) {
            if (gameViewModel.doesGameExist(gameInput.getName())) {
              Notification.show(
                  "A game with this name exists already, please change name",
                  5000,
                  Notification.Position.BOTTOM_STRETCH);
            } else {
              // Do nothing if currentUser is already in a game
              for (Game singleGame : gameViewModel.getAllGames()) {
                if (singleGame.contains(gameViewModel.getCurrentUser().getUsername())) {
                  Notification.show(
                      "You're already in a game and cannot create a new one.",
                      5000,
                      Notification.Position.BOTTOM_STRETCH);
                  create.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY));
                  return;
                }
              }

              gameViewModel.createGame(
                  gameInput.getName(),
                  gameInput.getMaxPlayers(),
                  gameInput.isLocked(),
                  gameInput.getPassword(),
                  Status.READY);
              Broadcaster.broadcast(
                  new ViewData<>(gameViewModel.getAllGames(), ViewData.Type.GAMES));
              create.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY));
            }
          } else {
            Notification.show(
                "Please fill out all fields", 5000, Notification.Position.BOTTOM_STRETCH);
          }
        });
    layout.add(cancel, create);
    return layout;
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
