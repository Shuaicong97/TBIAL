package de.lmu.ifi.sosy.tbial.views.dashboard.shared;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

public class GameImage extends Image {

  private static final String UNIT = "px";

  private static final String URL = "/dashboard/";

  public GameImage(GameImageEnum gameImage, int height, int width, String... cssClasses) {
    this.setSrc(
        new StreamResource(
            gameImage.getName(),
            () -> getClass().getResourceAsStream(URL + gameImage.getFileName())));
    this.setAlt(gameImage.getAlt());
    this.setHeight(height + UNIT);
    this.setWidth(width + UNIT);
    this.addClassNames(cssClasses);
  }
}
