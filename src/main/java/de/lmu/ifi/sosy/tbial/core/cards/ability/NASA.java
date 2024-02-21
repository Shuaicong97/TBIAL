package de.lmu.ifi.sosy.tbial.core.cards.ability;

import com.vaadin.flow.component.html.Image;
import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;
import java.util.UUID;
import javax.persistence.Entity;

@Entity
public class NASA extends Card {
  private final Image image;

  public NASA() {
    this.cardId = UUID.randomUUID();
    this.name = "NASA";
    this.image = ImageBuilder.getCardImage(name, Helpers.CARD_HEIGHT, Helpers.CARD_WIDTH);
  }

  @Override
  public Image getImage() {
    return image;
  }

  @Override
  public void executeAction() {}
}
