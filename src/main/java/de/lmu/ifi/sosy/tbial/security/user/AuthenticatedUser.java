// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.security.user;

import com.vaadin.flow.component.UI;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.UserViewModel;
import de.lmu.ifi.sosy.tbial.security.SecurityConfiguration;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Represents authenticated users. Instances of this class are created if a user visits the page.
 */
@Component
public class AuthenticatedUser {

  private final UserViewModel userViewModel;

  /**
   * Create an authenticated user from a registered user in the user repository, if present.
   *
   * @param userViewModel injected user repository, i.e., the connection to the database table
   */
  public AuthenticatedUser(@Autowired UserViewModel userViewModel) {
    this.userViewModel = userViewModel;
  }

  /**
   * If the current user is authenticated, return the authentication token.
   *
   * @return the authentication of the current user, if signed-in.
   */
  private Optional<Authentication> getAuthentication() {
    SecurityContext context = SecurityContextHolder.getContext();
    return Optional.ofNullable(context.getAuthentication())
        .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken));
  }

  /**
   * Get the User object mapping to the username in the authentication token.
   *
   * @return the user with the username in the authentication token, if such a user exists.
   */
  public Optional<User> get() {
    return getAuthentication()
        .map(authentication -> userViewModel.findBy(authentication.getName()));
  }

  /** Logs out a user by deleting the authentication. Redirects the user to the welcome page. */
  public void logout() {
    // set online status of current user to false
    userViewModel.setOnline(false);
    userViewModel.setAvailable(true);
    Broadcaster.broadcast(new ViewData<>(userViewModel.getOnlineUsers(), ViewData.Type.USERS));
    Broadcaster.broadcast(new ViewData<>(userViewModel.getAvailableUsers(), ViewData.Type.USERS));

    // redirect user to welcome page
    SecurityContextHolder.clearContext();
    UI.getCurrent().getPage().setLocation(SecurityConfiguration.LOGOUT_URL);
  }
}
