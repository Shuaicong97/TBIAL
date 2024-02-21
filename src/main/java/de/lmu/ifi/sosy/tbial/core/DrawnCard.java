package de.lmu.ifi.sosy.tbial.core;

import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.core.player.Player;

public class DrawnCard {

  private final Card card;
  private final Player player;

  public DrawnCard(Card card, Player player) {
    this.card = card;
    this.player = player;
  }

  public Card getCard() {
    return card;
  }

  public Player getPlayer() {
    return player;
  }
}
