package de.lmu.ifi.sosy.tbial.views.dashboard;

import static de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.DashboardCssEnum.*;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.views.MainLayout;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.CardTrayVerticalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.NameAndCharacterVerticalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.PlayableCardsVerticalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.StatusBarVerticalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.ConfigSizes;

@PageTitle(("PlayerDashboardRight"))
@Route(value = "playerDashboardRight", layout = MainLayout.class)
@AnonymousAllowed
@CssImport("./styles/dashboard.css")
public class DashboardRight extends HorizontalLayout {

  private static final long serialVersionUID = 6437333691282922374L;

  private final transient Player player;
  private final StatusBarVerticalComponent statusBarVerticalComponent;

  public DashboardRight(Player player) {
    this.player = player;
    statusBarVerticalComponent =
        new StatusBarVerticalComponent(
            player,
            ConfigSizes.getMentalHealthSize(),
            ConfigSizes.getCrownSize(),
            true,
            ROTATE270.getCssClass(),
            MARGIN_BRAIN_RIGHT.getCssClass(),
            MARGIN_CROWN_RIGHT.getCssClass(),
            MARGIN_PRESTIGE_AVATAR_RIGHT.getCssClass());
    CardTrayVerticalComponent cardTrayVerticalComponent =
        new CardTrayVerticalComponent(
            player,
            ConfigSizes.getCardTray240(),
            ConfigSizes.getCardTray50(),
            ConfigSizes.getImageSmallWidth(),
            ConfigSizes.getImageSmallHeight(),
            HORIZONTAL_GAME_DECK.getCssClass(),
            MARGIN_CARD_TRAY_RIGHT.getCssClass(),
            ROTATE270.getCssClass());
    PlayableCardsVerticalComponent playableCardsVerticalComponent =
        new PlayableCardsVerticalComponent(
            player,
            ConfigSizes.getGameDeck75(),
            ConfigSizes.getGameDeck240(),
            ROTATE270.getCssClass(),
            HORIZONTAL_GAME_DECK.getCssClass(),
            MARGIN_PLAYABLE_CARDS_RIGHTS.getCssClass(),
            POSITION_CARDS.getCssClass());
    NameAndCharacterVerticalComponent nameAndCharacterVerticalComponent =
        new NameAndCharacterVerticalComponent(
            player,
            ROTATE270.getCssClass(),
            FONT_COLOR.getCssClass(),
            FONT_SIZE_SMALL.getCssClass(),
            MARGIN_NAME_CHARACTER_RIGHT.getCssClass());

    add(
        statusBarVerticalComponent,
        cardTrayVerticalComponent,
        playableCardsVerticalComponent,
        nameAndCharacterVerticalComponent);

    setWidth("105%");
  }

  public Player getPlayer() {
    return player;
  }

  public void updateMentalHealth(Player player) {
    statusBarVerticalComponent.updateMentalHealth(player);
  }
}
