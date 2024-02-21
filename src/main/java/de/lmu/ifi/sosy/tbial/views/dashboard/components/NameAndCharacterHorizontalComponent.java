package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts.HorizontalLayoutBuilder;

public class NameAndCharacterHorizontalComponent extends HorizontalLayout {

  private final String[] cssClasses;

  public NameAndCharacterHorizontalComponent(Player player, String... cssClasses) {
    this.cssClasses = cssClasses;

    add(displayNameAndCharacter(player));
    setSizeUndefined();
  }

  private HorizontalLayout displayNameAndCharacter(Player player) {
    return new HorizontalLayoutBuilder()
        .withComponent(new Label(player.getUsername() + " :   " + player.getCharacter().getName()))
        .withCssClass(cssClasses[0])
        .withCssClass(cssClasses[1])
        .withCssClass(cssClasses[2])
        .withSizeUndefined()
        .build();
  }
}
