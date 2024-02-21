package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.GameImage;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.GameImageEnum;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.PrestigeAvatar;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts.VerticalLayoutBuilder;

public class StatusBarVerticalComponent extends VerticalLayout {

  private final int imageMentalHealthSize;
  private final int imageCrownSize;
  private final String[] cssClasses;
  private Registration broadcasterRegistration;
  private final boolean isPrestigeFirst;

  public StatusBarVerticalComponent(
      Player player,
      int imageMentalHealthSize,
      int imageCrownSize,
      boolean isPrestigeFirst,
      String... cssClasses) {
    this.imageMentalHealthSize = imageMentalHealthSize;
    this.imageCrownSize = imageCrownSize;
    this.cssClasses = cssClasses;
    this.isPrestigeFirst = isPrestigeFirst;
    displayStatusBar(isPrestigeFirst, player);

    // Dynamically update with player related values
    // TODO: This Listener is not used at all, no event of type players is ever sent out
    addAttachListener(
        e ->
            broadcasterRegistration =
                Broadcaster.register(
                    data ->
                        e.getUI()
                            .access(
                                () -> {
                                  if (data.getType() == ViewData.Type.PLAYERS) {
                                    displayStatusBar(isPrestigeFirst, player);
                                  }
                                })));
  }

  private void displayStatusBar(boolean isPrestigeFirst, Player player) {
    if (isPrestigeFirst) {
      add(displayPrestige(player), displayCrown(), displayMentalHealth(player));
    } else {
      add(displayMentalHealth(player), displayCrown(), displayPrestige(player));
    }
    setSizeUndefined();
  }

  private VerticalLayout displayMentalHealth(Player player) {
    VerticalLayoutBuilder verticalLayoutBuilder = new VerticalLayoutBuilder();
    // Mental health
    for (int i = 0; i < player.getHealthPoints(); i++) {
      GameImage gameImage =
          new GameImage(
              GameImageEnum.BRAIN, imageMentalHealthSize, imageMentalHealthSize, cssClasses[0]);
      verticalLayoutBuilder.withComponent(gameImage);
    }
    return verticalLayoutBuilder.withCssClass(cssClasses[1]).withSizeUndefined().build();
  }

  public void updateMentalHealth(Player player) {
    removeAll();
    displayStatusBar(isPrestigeFirst, player);
  }

  private VerticalLayout displayCrown() {
    // Crown for prestige
    return new VerticalLayoutBuilder()
        .withComponent(
            new GameImage(
                GameImageEnum.CROWN, imageCrownSize, (imageCrownSize + 10), cssClasses[0]))
        .withCssClass(cssClasses[2])
        .withSizeUndefined()
        .build();
  }

  private VerticalLayout displayPrestige(Player player) {
    // Prestige
    return new VerticalLayoutBuilder()
        .withComponent(new PrestigeAvatar(player.getPrestige(), cssClasses[0]))
        .withCssClass(cssClasses[3])
        .withSizeUndefined()
        .build();
  }
}
