package de.lmu.ifi.sosy.tbial.core;

public class ViewData<T> {
  public enum Type {
    GAMES,
    USERS,
    NOTIFICATION_INVITED,
    NOTIFICATION_JOINED,
    MESSAGE,
    DRAW_CARD,
    DISCARD_CARD,
    DISCARD_PILE,
    PLAYERS,
    PLAYER_LOST_LIFE,
    BUG_CARD_ADDED,
    BUG_CARD_REMOVED,
    BEING_ATTACKED,
    POP_UP_WINNER
  }

  private final T payload;

  private final Type type;

  public ViewData(T payload, Type type) {
    this.payload = payload;
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public T getPayload() {
    return payload;
  }
}
