package de.lmu.ifi.sosy.tbial.views.dashboard;

import static de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.DashboardCssEnum.*;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.views.MainLayout;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.*;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.ConfigSizes;

@PageTitle("PlayerDashboardLeft")
@Route(value = "playerDashboardLeft", layout = MainLayout.class)
@AnonymousAllowed
@CssImport("./styles/dashboard.css")
public class DashboardLeft extends HorizontalLayout {

  private static final long serialVersionUID = 6437333691282922374L;
  private final transient Player player;
  private final StatusBarVerticalComponent statusBarVerticalComponent;

  public DashboardLeft(Player player) {
    this.player = player;
    NameAndCharacterVerticalComponent nameAndCharacterVerticalComponent =
        new NameAndCharacterVerticalComponent(
            player,
            ROTATE90.getCssClass(),
            FONT_COLOR.getCssClass(),
            FONT_SIZE_SMALL.getCssClass(),
            MARGIN_NAME_CHARACTER_LEFT.getCssClass());
    PlayableCardsVerticalComponent playableCardsVerticalComponent =
        new PlayableCardsVerticalComponent(
            player,
            ConfigSizes.getGameDeck75(),
            ConfigSizes.getGameDeck240(),
            ROTATE90.getCssClass(),
            HORIZONTAL_GAME_DECK.getCssClass(),
            MARGIN_PLAYABLE_CARDS_LEFT.getCssClass(),
            POSITION_CARDS.getCssClass());
    CardTrayVerticalComponent cardTrayVerticalComponent =
        new CardTrayVerticalComponent(
            player,
            ConfigSizes.getCardTray240(),
            ConfigSizes.getCardTray50(),
            ConfigSizes.getImageSmallWidth(),
            ConfigSizes.getImageSmallHeight(),
            HORIZONTAL_GAME_DECK.getCssClass(),
            MARGIN_CARD_TRAY_LEFT.getCssClass(),
            ROTATE90.getCssClass());
    statusBarVerticalComponent =
        new StatusBarVerticalComponent(
            player,
            ConfigSizes.getMentalHealthSize(),
            ConfigSizes.getCrownSize(),
            false,
            ROTATE90.getCssClass(),
            MARGIN_BRAIN_LEFT.getCssClass(),
            MARGIN_CROWN_LEFT.getCssClass(),
            MARGIN_PRESTIGE_AVATAR_LEFT.getCssClass());

    add(
        nameAndCharacterVerticalComponent,
        playableCardsVerticalComponent,
        cardTrayVerticalComponent,
        statusBarVerticalComponent);

    setWidth("105%");
  }

  public Player getPlayer() {
    return player;
  }

  public void updateMentalHealth(Player player) {
    statusBarVerticalComponent.updateMentalHealth(player);
  }
}
