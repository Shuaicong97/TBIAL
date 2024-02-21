// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.views;

import com.google.common.collect.ImmutableSet;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.User.Type;
import de.lmu.ifi.sosy.tbial.core.user.UserRepository;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Register")
@Route(value = Helpers.REGISTER, layout = MainLayout.class)
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

  private static final long serialVersionUID = -8834317654392871335L;

  public RegisterView(@Autowired UserRepository repository) {
    // create the form
    TextField username = new TextField("Username");
    username.setValueChangeMode(ValueChangeMode.EAGER);
    TextField name = new TextField("Name");
    name.setValueChangeMode(ValueChangeMode.EAGER);
    PasswordField password = new PasswordField("Password");
    password.setValueChangeMode(ValueChangeMode.EAGER);
    Paragraph errorMessage = new Paragraph("Register to play 'The Bug is a Lie'");
    Button registerButton = new Button("Register");
    // register a click listener to the button
    // listener is executed iff button is clicked
    registerButton.addClickListener(
        l ->
            validateData(repository, username.getValue(), name.getValue(), password.getValue())
                .ifPresentOrElse(
                    // error is present:
                    // show the error message to the user
                    errorMessage::setText,
                    // no error is present:
                    // store the user to the database and navigate to the login page
                    () -> {
                      repository.save(
                          new User(
                              name.getValue(),
                              username.getValue(),
                              password.getValue(),
                              ImmutableSet.of(Type.USER),
                              false,
                              true));
                      UI.getCurrent().navigate(Helpers.LOGIN);
                    }));

    add(username, name, password, registerButton, errorMessage);

    // pressing enter allows clicking the button
    UI.getCurrent().addShortcutListener(registerButton::click, Key.ENTER);
  }

  /**
   * Validate the user data in the form.
   *
   * @param repository The database connection to all users
   * @param username The chosen username
   * @param name The real name of the user
   * @param password The password of the user
   * @return An Optional String if an error was detected, otherwise Optional#empty
   */
  private Optional<String> validateData(
      UserRepository repository, String username, String name, String password) {
    if (repository.findByUsername(username) != null) {
      return Optional.of("Username already taken");
    }
    if (username.isBlank()) {
      return Optional.of("Username must have at least 1 nonempty character");
    }
    if (name.isBlank()) {
      return Optional.of("Name must have at least 1 nonempty character");
    }
    if (password.length() < 7) {
      return Optional.of("Password needs at least 7 characters.");
    }
    return Optional.empty();
  }
}
