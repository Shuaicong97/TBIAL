package de.lmu.ifi.sosy.tbial.core.cards.action;

import com.vaadin.flow.component.html.Image;
import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;
import java.util.UUID;
import javax.persistence.Entity;

@Entity
public class CodeFixSession extends Card {
  private final Image image;

  public CodeFixSession() {
    this.cardId = UUID.randomUUID();
    this.name = "CodeFixSession";
    this.image = ImageBuilder.getCardImage(name, Helpers.CARD_HEIGHT, Helpers.CARD_WIDTH);
  }

  public Image getImage() {
    return image;
  }

  @Override
  public void executeAction() {}
}
