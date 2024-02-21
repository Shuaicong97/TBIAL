package de.lmu.ifi.sosy.tbial.utils;

import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.core.cards.ability.*;
import de.lmu.ifi.sosy.tbial.core.cards.action.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Helpers {

  public static final int OWN_DASHBOARD_COMPONENT_WIDTH = 400;
  public static final int OWN_DASHBOARD_GAME_DECK_HEIGHT = 120;
  public static final int OWN_DASHBOARD_CARD_TRAY_HEIGHT = 80;
  public static final int OWN_DASHBOARD_STATUS_BAR_ICON_SIZE = 18;
  public static final int CARD_WIDTH = 64;
  public static final int CARD_HEIGHT = 112;
  public static final int OWN_DASHBOARD_IMAGE_WIDTH = 50;
  public static final int OWN_DASHBOARD_IMAGE_HEIGHT = 75;

  private static final int BUG_APPEARANCE_IN_DECK = 4;
  private static final int SOLUTION_APPEARANCE_IN_DECK = 2;
  private static final int EXCUSES_APPEARANCE_IN_DECK = 4;
  private static final int SPECIAL_ACTION_CARDS_APPEARANCE_IN_DECK = 4;

  /** Routes used throughout the application. */
  public static final String ABOUT = "about";

  public static final String HOME = "home";
  public static final String GAME_BOARD = "game_board";
  public static final String LOBBY = "lobby";
  public static final String LOGIN = "login";
  public static final String PLAYERS_LIST = "players_list";
  public static final String REGISTER = "register";
  public static final String RULES = "rules";
  public static final String CREATE_GAME = "create_game";
  public static final String GAMES_LIST = "games_list";

  /**
   * Creates a stack of cards and returns it in a randomized order.
   *
   * @return The stack of playing cards
   */
  public static Stack<Card> getDeck() {
    List<Card> all = new ArrayList<>();
    Stack<Card> deck = new Stack<>();

    // ------ There are overall 24 bugs in the deck ------
    for (int j = 0; j < BUG_APPEARANCE_IN_DECK; j++) {
      all.add(new NullPointer());

      all.add(new OffByOne());
      all.add(new ClassNotFound());
      all.add(new SystemHangs());
      all.add(new CoreDump());
      all.add(new CustomerHatesUI());
    }

    // ------ There are overall 6 solutions in the deck ------
    for (int i = 0; i < SOLUTION_APPEARANCE_IN_DECK; i++) {
      all.add(new Coffee());
      all.add(new CodeFixSession());
      all.add(new IKnowRegularExpressions());
    }

    // ------ There are overall 12 excuses in the deck ------
    for (int i = 0; i < EXCUSES_APPEARANCE_IN_DECK; i++) {
      all.add(new WorksForMe());
      all.add(new ItsAFeature());
      all.add(new ImNotResponsible());
    }

    // ------ There are overall 20 special action cards in the deck ------
    for (int i = 0; i < SPECIAL_ACTION_CARDS_APPEARANCE_IN_DECK; i++) {
      all.add(new Pwnd());
      all.add(new IRefactoredYourCodeAway());
    }
    all.add(new SystemIntegration());
    all.add(new SystemIntegration());
    all.add(new SystemIntegration());

    all.add(new StandupMeeting());
    all.add(new StandupMeeting());

    all.add(new BoringMeeting());
    all.add(new BoringMeeting());

    all.add(new PersonalCoffeeMachine());
    all.add(new PersonalCoffeeMachine());

    all.add(new LANParty());
    all.add(new RedBullDispenser());
    all.add(new Heisenbug());

    // ------ There are overall 13 ability cards in the deck ------
    all.add(new BugDelegation());
    all.add(new BugDelegation());

    all.add(new NASA());

    all.add(new Google());
    all.add(new Google());

    all.add(new Microsoft());
    all.add(new Microsoft());
    all.add(new Microsoft());

    all.add(new Accenture());
    all.add(new Accenture());

    all.add(new WearsTieAtWork());
    all.add(new WearsTieAtWork());

    all.add(new WearsSunglassesAtWork());

    // ------ There are overall 4 ability cards in the deck ------
    // Not anymore ha-ha-ha!

    Collections.shuffle(all);
    deck.addAll(all);

    return deck;
  }
}
