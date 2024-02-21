package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.DiscardedCard;
import de.lmu.ifi.sosy.tbial.core.DrawnCard;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.config.ConfigSizes;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts.VerticalLayoutBuilder;

public class PlayableCardsVerticalComponent extends VerticalLayout {

  private final int gameDeckWidth;
  private final int gameDeckHeight;
  private final String[] cssClasses;

  private final VerticalLayout cardComponent;
  private Registration broadcasterRegistration;

  public PlayableCardsVerticalComponent(
      Player player, int gameDeckWidth, int gameDeckHeight, String... cssClasses) {
    this.gameDeckWidth = gameDeckWidth;
    this.gameDeckHeight = gameDeckHeight;
    this.cssClasses = cssClasses;
    this.cardComponent = getCardComponent();

    // Display the player's cards at the beginning of the game
    player.getCards().forEach(card -> add(onCardDrawn(new DrawnCard(card, player), player)));

    setSizeUndefined();

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
                                      add(onCardDrawn((DrawnCard) data.getPayload(), player));
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

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void onLifeTaken(Player player) {
    if (player.isDead()) {
      cardComponent.removeAll(); // Remove all cards from the hand
      Image img =
          ImageBuilder.getCardImage(
              player.getRole().getName(),
              ConfigSizes.getCardHeightVertical(),
              ConfigSizes.getCardWidth());
      cardComponent.add(img);
      img.addClassNames(cssClasses[0]);
    }
  }

  private VerticalLayout onCardDrawn(DrawnCard drawnCard, Player player) {
    Image img;
    if (player.getUsername().equals(drawnCard.getPlayer().getUsername())) {
      img =
          ImageBuilder.getCardImage(
              "BackSide", ConfigSizes.getCardHeightVertical(), ConfigSizes.getCardWidth());
      img.addClassNames(cssClasses[0]);

      cardComponent.add(img);

      updateOnCardsLessThanFive();
      updateOnCardsMoreThanFour();
    }
    return cardComponent;
  }

  private void updateOnCardsMoreThanFour() {
    if (cardComponent.getComponentCount() > 4) {
      Image first = (Image) cardComponent.getComponentAt(0);
      first.addClassName("firstCardMoreThanFour");
      first.removeClassNames("cardsLessThanFive");

      for (int i = 1; i < cardComponent.getComponentCount(); i++) {
        Image image = (Image) cardComponent.getComponentAt(i);
        image.removeClassNames("cardsLessThanFive");
        image.addClassNames("cardsMoreThanFiveVertical");
      }
    }
  }

  private void updateOnCardsLessThanFive() {
    if (cardComponent.getComponentCount() < 5) {
      for (int i = 0; i < cardComponent.getComponentCount(); i++) {
        Image image = (Image) cardComponent.getComponentAt(i);
        image.addClassNames("cardsLessThanFive");
        image.removeClassName("cardsMoreThanFiveVertical");
        image.removeClassName("firstCardMoreThanFour");
      }
    }
  }

  private void onCardDiscarded(DiscardedCard discardedCard, Player player) {
    if (discardedCard.getPlayer().getUsername().equals(player.getUsername())) {
      cardComponent.remove(cardComponent.getComponentAt(cardComponent.getComponentCount() - 1));

      updateOnCardsLessThanFive();
      updateOnCardsMoreThanFour();
    }
  }

  private VerticalLayout getCardComponent() {
    return new VerticalLayoutBuilder()
        .withHeight(gameDeckHeight)
        .withWidth(gameDeckWidth)
        .withCssClass(cssClasses[1])
        .withCssClass(cssClasses[2])
        .build();
  }
}
