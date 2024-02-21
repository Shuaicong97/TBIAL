package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.lmu.ifi.sosy.tbial.core.cards.Role;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts.HorizontalLayoutBuilder;

public class CardTrayHorizontalComponent extends HorizontalLayout {

  private final int cardTrayHeight;
  private final int cardTrayWidth;
  private final int imageWidth;
  private final int imageHeight;
  private final String[] cssClasses;

  public CardTrayHorizontalComponent(
      Player player,
      int cardTrayHeight,
      int cardTrayWidth,
      int imageWidth,
      int imageHeight,
      String... cssClasses) {
    this.cardTrayHeight = cardTrayHeight;
    this.cardTrayWidth = cardTrayWidth;
    this.imageHeight = imageHeight;
    this.imageWidth = imageWidth;
    this.cssClasses = cssClasses;
    add(cardTray(player));
    setSizeUndefined();
  }

  private HorizontalLayout cardTray(Player player) {
    Image imageCharacter =
        ImageBuilder.getCardImage(player.getCharacter().getName(), imageHeight, imageWidth);
    imageCharacter.addClassNames(cssClasses[1]);
    if (player.getRole() == Role.MANAGER) {
      Image imageRole =
          ImageBuilder.getCardImage(player.getRole().getName(), imageHeight, imageWidth);
      imageRole.addClassNames(cssClasses[1]);
      return new HorizontalLayoutBuilder()
          .withComponent(imageRole)
          .withComponent(imageCharacter)
          .withHeight(cardTrayHeight)
          .withWidth(cardTrayWidth)
          .withCssClass(cssClasses[0])
          .withCssClass(cssClasses[2])
          .build();
    }
    return new HorizontalLayoutBuilder()
        .withComponent(imageCharacter)
        .withHeight(cardTrayHeight)
        .withWidth(cardTrayWidth)
        .withCssClass(cssClasses[0])
        .withCssClass(cssClasses[2])
        .build();
  }
}
