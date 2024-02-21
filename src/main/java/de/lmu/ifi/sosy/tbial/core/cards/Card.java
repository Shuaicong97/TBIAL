package de.lmu.ifi.sosy.tbial.core.cards;

import com.vaadin.flow.component.html.Image;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Card {

  @Id
  @Column(unique = true)
  protected UUID cardId;

  protected String name;

  public Card() {}

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Card)) return false;
    Card card = (Card) o;
    return cardId.equals(card.cardId) && name.equals(card.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  public abstract Image getImage();

  public abstract void executeAction();
}
