package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.lmu.ifi.sosy.tbial.core.cards.Role;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts.VerticalLayoutBuilder;

public class CardTrayVerticalComponent extends VerticalLayout {

  private final int cardTrayHeight;
  private final int cardTrayWidth;
  private final int imageWidth;
  private final int imageHeight;
  private final String[] cssClasses;

  public CardTrayVerticalComponent(
      Player player,
      int cardTrayHeight,
      int cardTrayWidth,
      int imageWidth,
      int imageHeight,
      String... cssClasses) {
    this.cardTrayHeight = cardTrayHeight;
    this.cardTrayWidth = cardTrayWidth;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.cssClasses = cssClasses;
    add(cardTray(player));
    setSizeUndefined();
  }

  private VerticalLayout cardTray(Player player) {
    Image imageCharacter =
        ImageBuilder.getCardImage(player.getCharacter().getName(), imageHeight, imageWidth);
    imageCharacter.addClassNames(cssClasses[2], "moveCardTrayCards");
    if (player.getRole() == Role.MANAGER) {
      Image imageRole =
          ImageBuilder.getCardImage(player.getRole().getName(), imageHeight, imageWidth);
      imageRole.addClassNames(cssClasses[2], "moveCardTrayCards");
      return new VerticalLayoutBuilder()
          .withComponent(imageRole)
          .withComponent(imageCharacter)
          .withHeight(cardTrayHeight)
          .withWidth(cardTrayWidth)
          .withCssClass(cssClasses[0])
          .withCssClass(cssClasses[1])
          .build();
    }
    return new VerticalLayoutBuilder()
        .withComponent(imageCharacter)
        .withHeight(cardTrayHeight)
        .withWidth(cardTrayWidth)
        .withCssClass(cssClasses[0])
        .withCssClass(cssClasses[1])
        .build();
  }
}
