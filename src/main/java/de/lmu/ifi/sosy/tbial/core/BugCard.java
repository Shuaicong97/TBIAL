package de.lmu.ifi.sosy.tbial.core;

import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.core.player.Player;

public class BugCard {
  private final Card card;
  private final Player trigger;
  private final Player receiver;

  public BugCard(Card card, Player trigger, Player receiver) {
    this.card = card;
    this.trigger = trigger;
    this.receiver = receiver;
  }

  public Card getCard() {
    return card;
  }

  public Player getTrigger() {
    return trigger;
  }

  public Player getReceiver() {
    return receiver;
  }
}
