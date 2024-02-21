package de.lmu.ifi.sosy.tbial.views.dashboard.shared.config;

public enum DashboardCssEnum {
  LEER("leer"),
  HORIZONTAL_GAME_DECK("horizontalGameDeck"),
  ROTATE90("rotate90"),
  ROTATE180("rotate180"),
  ROTATE270("rotate270"),
  MARGIN_BRAIN_LEFT("marginBrainLeft"),
  MARGIN_CARD_TRAY_LEFT("marginCardTrayLeft"),
  MARGIN_NAME_CHARACTER_LEFT("marginNameCharacterLeft"),
  MARGIN_PLAYABLE_CARDS_LEFT("marginPlayableCardsLeft"),
  MARGIN_CROWN_LEFT("marginCrownLeft"),
  MARGIN_PRESTIGE_AVATAR_LEFT("marginPrestigeAvatarLeft"),
  MARGIN_CARD_TRAY_TOP("marginCardTrayTop"),
  MARGIN_PLAYABLE_CARDS_TOP("marginPlayableCardsTop"),
  MARGIN_CROWN_TOP("marginCrownTop"),
  MARGIN_CARD_TRAY_RIGHT("marginCardTrayRight"),
  MARGIN_NAME_CHARACTER_RIGHT("marginNameCharacterRight"),
  MARGIN_PLAYABLE_CARDS_RIGHTS("marginPlayableCardsRight"),
  MARGIN_BRAIN_RIGHT("marginBrainRight"),
  MARGIN_CROWN_RIGHT("marginCrownRight"),
  MARGIN_PRESTIGE_AVATAR_RIGHT("marginPrestigeAvatarRight"),
  MARGIN_NAME_CHARACTER_OWN("marginNameCharacterOwn"),
  MARGIN_CROWN_OWN("marginCrownOwn"),
  FONT_COLOR("fontColor"),
  FONT_SIZE_SMALL("fontSizeSmall"),
  FONT_SIZE_OWN("fontSizeOwn"),
  POSITION_CARDS("positionCards"),
  MOVE_CARDS("moveCards"),
  MOVE_CARD_TRAY_CARDS("moveCardTrayCards"),
  MOVE_CARD_TRAY_CARD_TOP("moveCardTrayCardsTop"),
  MOVE_CARD_TRAY_CARD_OWN("moveCardTrayCardsOwn");

  private final String cssClass;

  DashboardCssEnum(String cssClass) {
    this.cssClass = cssClass;
  }

  public String getCssClass() {
    return cssClass;
  }
}
