// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.core.user.UserViewModel;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

// set the title of the page (title in tab description)
@PageTitle("The bug is a lie!")
// define the root: this page is reachable with localhost:port/index
@Route(value = Helpers.HOME, layout = MainLayout.class)
// creates an alias: this page is also reachable from localhost:port/
@RouteAlias(value = "", layout = MainLayout.class)
// this page can be viewed by anyone: Use @PermitAll to only allow signed-in users (regardless of
// their role)
@AnonymousAllowed
public class HomeView extends VerticalLayout {

  private static final long serialVersionUID = -4359018602732666410L;

  /** The welcome page of the website. */
  public HomeView(@Autowired UserViewModel userViewModel, @Autowired GameViewModel gameViewModel) {
    H1 title = new H1("The bug is a lie!");

    // greet user if signed-in else remind them to sign in.
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    final Paragraph signInText;
    if (authentication.getPrincipal() instanceof User && authentication.isAuthenticated()) {
      User user = ((User) authentication.getPrincipal());
      signInText =
          new Paragraph(
              "Welcome, "
                  + user.getUsername()
                  + "! Create or join a game and start playing \uD83C\uDFAE");
      // Set user to be online & available if they're not in game
      userViewModel.setOnline(true);
      gameViewModel
          .getCurrentGame()
          .ifPresent(
              game ->
                  userViewModel.setAvailable(
                      !game.contains(gameViewModel.getCurrentUser().getUsername())));

      Broadcaster.broadcast(new ViewData<>(userViewModel.getOnlineUsers(), ViewData.Type.USERS));
      Broadcaster.broadcast(new ViewData<>(userViewModel.getAvailableUsers(), ViewData.Type.USERS));
    } else {
      signInText = new Paragraph("Sign-in to start playing \uD83D\uDE09");
    }

    setMargin(true);
    //    setVerticalComponentAlignment(Alignment.END, title, signInText);

    setAlignItems(Alignment.CENTER);
    add(title, signInText);
  }
}
