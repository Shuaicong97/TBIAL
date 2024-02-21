package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import static de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.DashboardCssEnum.ROTATE180;

import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.DiscardedCard;
import de.lmu.ifi.sosy.tbial.core.DrawnCard;
import de.lmu.ifi.sosy.tbial.core.game.Game;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.ConfigSizes;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts.HorizontalLayoutBuilder;

public class PlayableCardsHorizontalComponent extends HorizontalLayout {

  private final int gameDeckWidth;
  private final int gameDeckHeight;
  private final int imageWidth;
  private final int imageHeight;
  private final String[] cssClasses;
  private final HorizontalLayout cardComponent;

  private final boolean isOwnDashboard;
  private Registration broadcasterRegistration;

  public PlayableCardsHorizontalComponent(
      Player player,
      Game game,
      int gameDeckWidth,
      int gameDeckHeight,
      int imageWidth,
      int imageHeight,
      boolean isOwnDashboard,
      String... cssClasses) {
    this.gameDeckWidth = gameDeckWidth;
    this.gameDeckHeight = gameDeckHeight;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.cssClasses = cssClasses;
    this.isOwnDashboard = isOwnDashboard;
    this.cardComponent = buildCardComponent();

    // Display the player's cards at the beginning of the game (simulate as if those were drawn
    // cards)
    player.getCards().forEach(card -> onCardDrawn(new DrawnCard(card, player), game, player));
    add(cardComponent);
    if (isOwnDashboard) {
      add(buildRoleComponent(player));
    }
    setSizeUndefined();

    attachListener(game, player);

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void attachListener(Game game, Player player) {
    // Update the player's cards dynamically throughout the game
    addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  switch (data.getType()) {
                                    case DRAW_CARD:
                                      onCardDrawn((DrawnCard) data.getPayload(), game, player);
                                      break;
                                    case DISCARD_CARD:
                                      onCardDiscarded((DiscardedCard) data.getPayload(), player);
                                      break;
                                    case PLAYER_LOST_LIFE:
                                      onLifeTaken(player);
                                      break;
                                    default:
                                  }
                                })));
  }

  private void onLifeTaken(Player player) {
    if (player.isDead()) {
      cardComponent.removeAll(); // Remove all cards from the players hand
      Image img = ImageBuilder.getCardImage(player.getRole().getName(), imageHeight, imageWidth);
      if (isOwnDashboard) {
        ImageBuilder.getCardImage(player.getRole().getName(), imageHeight, imageWidth);
      } else {
        ImageBuilder.getCardImage(
            player.getRole().getName(),
            ConfigSizes.getCardHeightVertical(),
            ConfigSizes.getCardWidth());
        img.addClassNames(ROTATE180.getCssClass());
      }
      cardComponent.add(img);
    }
  }

  public void onCardDrawn(DrawnCard drawnCard, Game game, Player player) {
    if (player.getUsername().equals(drawnCard.getPlayer().getUsername())) {
      Image img;
      if (isOwnDashboard && !player.isDead()) {
        img = drawnCard.getCard().getImage();
        img.addClassName("moveCards");
        img.addClickListener(
            event ->
                game.discardToPile(
                    player.removeFromHand(drawnCard.getCard()), drawnCard.getPlayer()));
        DragSource<Image> card = DragSource.create(img);
        card.setDragData(drawnCard);
        cardComponent.add(img);
      } else {
        img =
            ImageBuilder.getCardImage(
                "BackSide", ConfigSizes.getCardHeightHorizontal(), ConfigSizes.getCardWidth());
        img.addClassNames(cssClasses[1]);
        cardComponent.add(img);
      }

      updateOnCardsLessThanFive();
      updateOnCardsMoreThanFour();
    }
  }

  private void updateOnCardsMoreThanFour() {
    if (cardComponent.getComponentCount() > 4) {

      for (int i = 1; i < cardComponent.getComponentCount(); i++) {
        Image image = (Image) cardComponent.getComponentAt(i);
        if (!isOwnDashboard) image.addClassNames("cardsMoreThanFiveHorizontal");
      }
    }
  }

  private void updateOnCardsLessThanFive() {
    if (cardComponent.getComponentCount() < 5) {
      for (int i = 0; i < cardComponent.getComponentCount(); i++) {
        Image image = (Image) cardComponent.getComponentAt(i);
        image.removeClassName("cardsMoreThanFiveHorizontal");
        image.removeClassName("firstCardMoreThanFourHorizontal");
      }
    }
  }

  private void onCardDiscarded(DiscardedCard discardedCard, Player player) {
    if (discardedCard.getPlayer().getUsername().equals(player.getUsername())) {
      Image img;
      if (isOwnDashboard) {
        // Remove the discarded card from the own hand
        img = discardedCard.getCard().getImage();
        img.removeClassName("moveCards");
        cardComponent.remove(img);
      } else {
        if (!player.isDead()) {
          // The top player discarded a card
          img = (Image) cardComponent.getComponentAt(cardComponent.getComponentCount() - 1);
          cardComponent.remove(img);
        }
      }

      updateOnCardsLessThanFive();
      updateOnCardsMoreThanFour();
    }
  }

  private HorizontalLayout buildCardComponent() {
    return new HorizontalLayoutBuilder()
        .withHeight(gameDeckHeight)
        .withWidth(gameDeckWidth)
        .withCssClass(cssClasses[0])
        .withCssClass(cssClasses[2])
        .build();
  }

  private HorizontalLayout buildRoleComponent(Player player) {
    return new HorizontalLayoutBuilder()
        .withComponent(
            ImageBuilder.getCardImage(player.getRole().getName(), imageHeight, imageWidth))
        .build();
  }

  public void enableOwnDashboardOnTurn(boolean isOnTurn) {
    for (int i = 0; i < cardComponent.getComponentCount(); i++) {
      Image image = (Image) cardComponent.getComponentAt(i);
      image.setEnabled(isOwnDashboard && isOnTurn);
    }
  }

  public HorizontalLayout getCardComponent() {
    return cardComponent;
  }
}
