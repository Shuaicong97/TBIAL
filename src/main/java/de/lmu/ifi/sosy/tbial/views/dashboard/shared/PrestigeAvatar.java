package de.lmu.ifi.sosy.tbial.views.dashboard.shared;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;

public class PrestigeAvatar extends Avatar {

  private static final int BLUE_COLOR = 5;

  public PrestigeAvatar(int prestige, String... cssClasses) {
    this.setName(String.valueOf(prestige));
    this.setColorIndex(BLUE_COLOR);
    this.addThemeVariants(AvatarVariant.LUMO_XSMALL);
    this.addClassNames(cssClasses);
  }
}
