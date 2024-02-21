package de.lmu.ifi.sosy.tbial.views.dashboard;

import static de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.DashboardCssEnum.*;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.lmu.ifi.sosy.tbial.core.game.Game;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.views.MainLayout;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.CardTrayHorizontalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.NameAndCharacterHorizontalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.PlayableCardsHorizontalComponent;
import de.lmu.ifi.sosy.tbial.views.dashboard.components.StatusBarHorizontalComponent;

@PageTitle("MyDashboard")
@Route(value = "myDashboard", layout = MainLayout.class)
@AnonymousAllowed
@CssImport("./styles/dashboard.css")
public class OwnDashboard extends VerticalLayout {

  private static final long serialVersionUID = 6437333691282922374L;

  private PlayableCardsHorizontalComponent playableCardsHorizontalComponent;

  private final transient Player player;

  private final StatusBarHorizontalComponent statusBarHorizontalComponent;

  public OwnDashboard(Player player, Game game) {
    this.player = player;
    statusBarHorizontalComponent = createStatusBarComponent(player);
    add(
        statusBarHorizontalComponent,
        createCardTrayComponent(player),
        createPlayableCardsComponent(player, game),
        createNameAndCharacterComponent(player));
  }

  private PlayableCardsHorizontalComponent createPlayableCardsComponent(Player player, Game game) {
    playableCardsHorizontalComponent =
        new PlayableCardsHorizontalComponent(
            player,
            game,
            Helpers.OWN_DASHBOARD_COMPONENT_WIDTH,
            Helpers.OWN_DASHBOARD_GAME_DECK_HEIGHT,
            Helpers.CARD_WIDTH,
            Helpers.CARD_HEIGHT,
            true,
            HORIZONTAL_GAME_DECK.getCssClass(),
            LEER.getCssClass(),
            LEER.getCssClass(),
            MOVE_CARD_TRAY_CARD_OWN.getCssClass());
    return playableCardsHorizontalComponent;
  }

  private StatusBarHorizontalComponent createStatusBarComponent(Player player) {
    return new StatusBarHorizontalComponent(
        player,
        Helpers.OWN_DASHBOARD_STATUS_BAR_ICON_SIZE,
        Helpers.OWN_DASHBOARD_STATUS_BAR_ICON_SIZE,
        false,
        LEER.getCssClass(),
        MARGIN_CROWN_OWN.getCssClass());
  }

  private CardTrayHorizontalComponent createCardTrayComponent(Player player) {
    return new CardTrayHorizontalComponent(
        player,
        Helpers.OWN_DASHBOARD_CARD_TRAY_HEIGHT,
        Helpers.OWN_DASHBOARD_COMPONENT_WIDTH,
        Helpers.OWN_DASHBOARD_IMAGE_WIDTH,
        Helpers.OWN_DASHBOARD_IMAGE_HEIGHT,
        HORIZONTAL_GAME_DECK.getCssClass(),
        LEER.getCssClass(),
        LEER.getCssClass());
  }

  private NameAndCharacterHorizontalComponent createNameAndCharacterComponent(Player player) {
    return new NameAndCharacterHorizontalComponent(
        player,
        FONT_COLOR.getCssClass(),
        FONT_SIZE_OWN.getCssClass(),
        MARGIN_NAME_CHARACTER_OWN.getCssClass());
  }

  public PlayableCardsHorizontalComponent getPlayableCardsHorizontalComponent() {
    return playableCardsHorizontalComponent;
  }

  public Player getPlayer() {
    return player;
  }

  public void updateMentalHealth(Player player) {
    statusBarHorizontalComponent.updateMentalHealth(player);
  }
}
