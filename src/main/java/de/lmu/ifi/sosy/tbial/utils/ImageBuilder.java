package de.lmu.ifi.sosy.tbial.utils;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

@CssImport("./styles/gameBoard.css")
public class ImageBuilder {

  public static Image getCardImage(String name, int height, int width) {
    StreamResource imageResource =
        new StreamResource(
            name, () -> ImageBuilder.class.getResourceAsStream("/images/" + name + ".png"));
    Image image = new Image(imageResource, name);
    image.addClassNames("zoom");
    image.setWidth(width + "px");
    image.setHeight(height + "px");
    return image;
  }
}
