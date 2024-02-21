package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.GameImage;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.GameImageEnum;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.PrestigeAvatar;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts.HorizontalLayoutBuilder;

public class StatusBarHorizontalComponent extends HorizontalLayout {

  private final int imageMentalHealthSize;
  private final int imageCrownSize;
  private final String[] cssClasses;
  private Registration broadcasterRegistration;
  private final boolean isPrestigeFirst;

  public StatusBarHorizontalComponent(
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

    addDetachListener(
        e -> {
          broadcasterRegistration.remove();
          broadcasterRegistration = null;
        });
  }

  private void displayStatusBar(boolean isPrestigeFirst, Player player) {
    if (isPrestigeFirst) {
      add(displayPrestige(player), displayCrown(), displayMentalHealth(player));
    } else {
      add(displayMentalHealth(player), displayCrown(), displayPrestige(player));
    }
    setSizeUndefined();
  }

  public HorizontalLayout displayMentalHealth(Player player) {
    HorizontalLayoutBuilder horizontalLayoutBuilder = new HorizontalLayoutBuilder();
    // Mental health
    for (int i = 0; i < player.getHealthPoints(); i++) {
      GameImage gameImage =
          new GameImage(
              GameImageEnum.BRAIN, imageMentalHealthSize, imageMentalHealthSize, cssClasses[0]);
      horizontalLayoutBuilder.withComponent(gameImage);
    }
    return horizontalLayoutBuilder.withSizeUndefined().build();
  }

  public void updateMentalHealth(Player player) {
    removeAll();
    displayStatusBar(isPrestigeFirst, player);
  }

  private HorizontalLayout displayCrown() {
    // Crown for prestige
    return new HorizontalLayoutBuilder()
        .withComponent(
            new GameImage(
                GameImageEnum.CROWN,
                imageCrownSize,
                (imageCrownSize + 10),
                cssClasses[0],
                cssClasses[1]))
        .withSizeUndefined() // when no size is defined
        .build();
  }

  private HorizontalLayout displayPrestige(Player player) {
    // Prestige
    return new HorizontalLayoutBuilder()
        .withComponent(new PrestigeAvatar(player.getPrestige(), cssClasses[0]))
        .withSizeUndefined()
        .build();
  }
}
