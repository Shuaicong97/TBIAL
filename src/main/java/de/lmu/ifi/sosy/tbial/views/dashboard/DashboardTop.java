package de.lmu.ifi.sosy.tbial.views.dashboard;

import static de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.DashboardCssEnum.*;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.lmu.ifi.sosy.tbial.core.game.Game;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.views.MainLayout;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.CardTrayHorizontalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.NameAndCharacterHorizontalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.PlayableCardsHorizontalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.StatusBarHorizontalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.ConfigSizes;

@PageTitle("PlayerDashboardTop")
@Route(value = "playerDashboardTop", layout = MainLayout.class)
@AnonymousAllowed
@CssImport("./styles/dashboard.css")
public class DashboardTop extends VerticalLayout {

  private static final long serialVersionUID = 6437333691282922374L;

  private static final int CARD_HEIGHT = 66;
  private static final int CARD_WIDTH = 40;

  private final transient Player player;
  private final StatusBarHorizontalComponent statusBarHorizontalComponent;

  public DashboardTop(Player player, Game game) {
    this.player = player;
    statusBarHorizontalComponent =
        new StatusBarHorizontalComponent(
            player,
            ConfigSizes.getMentalHealthSize(),
            ConfigSizes.getCrownSize(),
            true,
            ROTATE180.getCssClass(),
            MARGIN_CROWN_TOP.getCssClass());
    CardTrayHorizontalComponent cardTrayHorizontalComponent =
        new CardTrayHorizontalComponent(
            player,
            ConfigSizes.getCardTray50(),
            ConfigSizes.getCardTray240(),
            ConfigSizes.getImageSmallWidth(),
            ConfigSizes.getImageSmallHeight(),
            HORIZONTAL_GAME_DECK.getCssClass(),
            ROTATE180.getCssClass(),
            MARGIN_CARD_TRAY_TOP.getCssClass());
    NameAndCharacterHorizontalComponent nameAndCharacterHorizontalComponent =
        new NameAndCharacterHorizontalComponent(
            player, FONT_COLOR.getCssClass(), FONT_SIZE_SMALL.getCssClass(), LEER.getCssClass());
    PlayableCardsHorizontalComponent playableCardsHorizontalComponent =
        new PlayableCardsHorizontalComponent(
            player,
            game,
            ConfigSizes.getGameDeck240(),
            ConfigSizes.getGameDeck75(),
            CARD_WIDTH,
            CARD_HEIGHT,
            false,
            HORIZONTAL_GAME_DECK.getCssClass(),
            ROTATE180.getCssClass(),
            MARGIN_PLAYABLE_CARDS_TOP.getCssClass(),
            MOVE_CARD_TRAY_CARD_TOP.getCssClass());

    add(
        nameAndCharacterHorizontalComponent,
        playableCardsHorizontalComponent,
        cardTrayHorizontalComponent,
        statusBarHorizontalComponent);
    setSizeUndefined();
  }

  public StatusBarHorizontalComponent getStatusBarHorizontalComponent() {
    return statusBarHorizontalComponent;
  }

  public Player getPlayer() {
    return player;
  }

  public void updateMentalHealth(Player player) {
    statusBarHorizontalComponent.updateMentalHealth(player);
  }
}
