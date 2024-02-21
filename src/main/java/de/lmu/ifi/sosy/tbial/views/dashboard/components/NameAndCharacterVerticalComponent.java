package de.lmu.ifi.sosy.tbial.views.dashboard.components;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.lmu.ifi.sosy.tbial.core.player.Player;

public class NameAndCharacterVerticalComponent extends VerticalLayout {

  private final String rotatePosition;
  private final String[] cssClasses;

  public NameAndCharacterVerticalComponent(Player player, String... cssClasses) {
    this.rotatePosition = cssClasses[0];
    this.cssClasses = cssClasses;

    add(displayNameAndCharacter(player));

    setWidth("100px");
    setHeight("240px");
    addClassNames(cssClasses[1], cssClasses[2]);
  }

  private Label displayNameAndCharacter(Player player) {
    Label nameAndCharacter =
        new Label(player.getUsername() + " :   " + player.getCharacter().getName());
    nameAndCharacter.addClassNames(rotatePosition, cssClasses[3]);
    nameAndCharacter.setWidth("240px");
    return nameAndCharacter;
  }
}
