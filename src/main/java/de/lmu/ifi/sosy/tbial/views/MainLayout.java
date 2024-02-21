// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.security.user.AuthenticatedUser;
import de.lmu.ifi.sosy.tbial.views.game.CreateGameView;
import de.lmu.ifi.sosy.tbial.views.game.ListOfGamesView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** The main view is a top-level placeholder for other views. */
@PageTitle("Main")
public class MainLayout extends AppLayout {

  private static final long serialVersionUID = -2457910575856012595L;
  private Registration broadcasterRegistration;

  public MainLayout(
      AuthenticatedUser pAuthenticatedUser,
      AccessAnnotationChecker pAccessChecker,
      GameViewModel gameViewModel) {
    addAttachListener(
        e ->
            Broadcaster.register(
                data ->
                    getUI()
                        .ifPresent(
                            ui -> {
                              switch (data.getType()) {
                                case NOTIFICATION_JOINED:
                                  ui.access(gameViewModel::showJoinNotification);
                                  break;
                                case NOTIFICATION_INVITED:
                                  ui.access(gameViewModel::showInvitationNotification);
                                  break;
                                default:
                              }
                            })));
    addToNavbar(createHeaderContent(pAuthenticatedUser, pAccessChecker, gameViewModel));
  }

  /**
   * Create the header of the webpage based on the current user, if signed in.
   *
   * @param authenticatedUser wrapper for authenticated users
   * @param accessChecker checker used to hide inaccessible views
   * @return a component representing the header of the webpage
   */
  private Component createHeaderContent(
      AuthenticatedUser authenticatedUser,
      AccessAnnotationChecker accessChecker,
      GameViewModel gameViewModel) {
    Header header = new Header();
    header.addClassNames(
        "bg-base", "border-b", "border-contrast-10", "box-border", "flex", "flex-col", "w-full");
    Div layout = new Div();
    layout.addClassNames("flex", "h-xl", "items-center", "px-l");

    H1 appName = new H1("\uD83D\uDC1B is a lie!");
    appName.addClassNames("my-0", "me-auto", "text-l");
    layout.add(appName);

    authenticatedUser
        .get()
        .ifPresentOrElse(
            user -> handleAuthenticated(layout, user, authenticatedUser),
            () -> handleUnauthenticated(layout));

    header.add(layout, createNavBar(accessChecker, gameViewModel));
    return header;
  }

  private void handleAuthenticated(Div layout, User user, AuthenticatedUser authenticatedUser) {
    Avatar avatar = new Avatar(user.getName());
    avatar.addClassNames("me-xs", "clickable");

    ContextMenu userMenu = new ContextMenu(avatar);
    userMenu.setOpenOnClick(true);
    userMenu.addItem("Logout", event -> authenticatedUser.logout());

    Span name = new Span(user.getName());
    name.addClassNames("font-medium", "text-s", "text-secondary");

    layout.add(avatar, name);
  }

  private void handleUnauthenticated(Div layout) {
    Anchor loginLink = new Anchor("login", "Sign in");
    loginLink.addClassNames("me-m");

    Anchor registerLink = new Anchor("register", "Register");

    layout.add(loginLink, registerLink);
  }

  private Component createNavBar(
      AccessAnnotationChecker accessChecker, GameViewModel gameViewModel) {
    Nav nav = new Nav();
    nav.addClassNames("flex", "gap-s", "overflow-auto", "px-m");

    // Wrap the links in a list; improves accessibility
    UnorderedList navLinks = new UnorderedList();
    navLinks.addClassNames("flex", "list-none", "m-0", "p-0");
    nav.add(navLinks);

    for (RouterLink link : createLinks(accessChecker, gameViewModel)) {
      ListItem item = new ListItem(link);

      navLinks.add(item);
    }
    return nav;
  }

  /**
   * Create links to all views contained in the menu items.
   *
   * @param accessChecker checker used to hide inaccessible views
   * @return All links to the provided views
   */
  private List<RouterLink> createLinks(
      AccessAnnotationChecker accessChecker, GameViewModel gameViewModel) {
    List<RouterLink> links = new ArrayList<>();
    createLink(accessChecker, "la la-globe", "Home", HomeView.class, gameViewModel)
        .ifPresent(links::add);
    createLink(accessChecker, "la la-address-card", "About", AboutView.class, gameViewModel)
        .ifPresent(links::add);
    createLink(accessChecker, "la la-address-card", "Rules", RulesTab.class, gameViewModel)
        .ifPresent(links::add);
    createLink(
            accessChecker, "la la-plus-circle", "Create Game", CreateGameView.class, gameViewModel)
        .ifPresent(links::add);
    createLink(accessChecker, "la la-users", "Lobby", LobbyView.class, gameViewModel)
        .ifPresent(links::add);
    createLink(accessChecker, "la la-stream", "Show Games", ListOfGamesView.class, gameViewModel)
        .ifPresent(links::add);
    createLink(accessChecker, "la la-user", "Show Players", PlayersListView.class, gameViewModel)
        .ifPresent(links::add);
    return links;
  }

  /**
   * Create a link to a view
   *
   * @param accessChecker checker used to hide inaccessible views
   * @param iconClass Vaadin icons for header elements
   * @param text Text next to the icon
   * @param redirectView View to redirect to
   * @return the link to the view
   */
  private Optional<RouterLink> createLink(
      AccessAnnotationChecker accessChecker,
      String iconClass,
      String text,
      Class<? extends Component> redirectView,
      GameViewModel gameViewModel) {
    if (!accessChecker.hasAccess(redirectView)) {
      return Optional.empty();
    }
    RouterLink link = new RouterLink();
    link.addClassNames("flex", "h-m", "items-center", "px-s", "relative", "text-secondary");
    link.setRoute(redirectView);

    // Lobby menu is visible only if the player is in a game.
    if (text.equals("Lobby")) {
      link.setVisible(false);
      link.addAttachListener(
          e ->
              broadcasterRegistration =
                  Broadcaster.register(
                      data ->
                          e.getUI()
                              .access(
                                  () ->
                                      gameViewModel
                                          .getCurrentGame()
                                          .ifPresent(
                                              game -> {
                                                if (game.contains(
                                                    gameViewModel.getCurrentUser().getUsername())) {
                                                  link.setVisible(true);
                                                }
                                              }))));
    }

    Span icon = new Span();
    icon.addClassNames("me-s", "text-l");
    if (!iconClass.isEmpty()) {
      icon.addClassNames(iconClass);
    }

    Span spanText = new Span(text);
    spanText.addClassNames("font-medium", "text-s", "whitespace-nowrap");

    link.add(icon, spanText);
    link.addClassName("link-hover");
    return Optional.of(link);
  }
}
