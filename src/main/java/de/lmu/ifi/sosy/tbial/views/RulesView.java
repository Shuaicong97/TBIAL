package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.lmu.ifi.sosy.tbial.utils.ImageBuilder;

public class RulesView extends VerticalLayout {

  public RulesView() {
    add(new H4("About the Game"));
    add(
        new Paragraph(
            "The Bug is a Lie is a card game for software engineers. It is a parody on the way"
                + "software is developed in industry with a sarcastic view on the individual roles people"
                + "take (Manager, Honest Developers and Evil Code Monkeys, and Consultants) and"
                + "how they, more often than not, work against each other."
                + "In the game, each player takes on one of these roles and tries to achieve a role"
                + "specific goal. For example, the consultant tries to take over the company, while"
                + "an evil code monkey tries to get the manager fired. Additionally, each player is"
                + "assigned a character by chance, which provides special abilities."
                + "The main battle ground revolves around bug reports, code fixes, and lame excuses"
                + "why a bug cannot be fixed. Each player has a certain amount of mental health"
                + "which gets down- or upgraded depending on the cards played. If mental health is"
                + "zero, the player gets fired (and has to leave the game)."
                + "An additional twist in the game stems from the fact that only the role of the"
                + "manager is known at the beginning of the game; all other roles are hidden and"
                + "must be inferred from the actions of a player. Furthermore, players can build up"
                + "prestige for defense against bug reports, and have a number of additional options"
                + "for attacking other players."));
    add(new H4("Preparation"));
    add(new Paragraph("The use of role cards depends on how many players take part in the game."));
    add(new Paragraph("4 Players: 1 Manager, 1 Consultant, 2 Evil Code Monkeys"));
    add(
        new Paragraph(
            "5 Players: 1 Manager, 1 Consultant, 2 Evil Code Monkeys, 1 Honest Developer"));
    add(
        new Paragraph(
            "6 Players: 1 Manager, 1 Consultant, 3 Evil Code Monkeys, 1 Honest Developer"));
    add(
        new Paragraph(
            "7 Players: 1 Manager, 1 Consultant, 3 Evil Code Monkeys, 2 Honest Developers"));
    add(
        new Paragraph(
            "All other role cards are irrelevant for the game. The game is started by the following action:"));
    add(
        new Paragraph(
            "⇒ Each player draws one character blindly. The manager must identify himself,"
                + "but no-one else may do so."
                + "Every person also has a certain character; there are 13 character cards in the game. Thus:"));
    add(
        new Paragraph(
            "⇒ Every player draws one character card blindly. Each player must place this"
                + "card publicly in front of him or her."
                + "At the beginning of the game, each player has zero prestige and the number of"
                + "mental health points indicated on his character card. The manager has one more"
                + "than indicated on the character card. Third,"));
    add(
        new Paragraph(
            "⇒ The action, ability and stumbling block cards are shuffled and piled up to"
                + "build the stack."));
    add(
        new Paragraph(
            "⇒ Every player takes as many cards from the stack as he has mental health"
                + "points."));
    add(
        new Paragraph(
            "Now, the actual game can begin. See section role cards for the goal of each player"
                + "according to his role."));

    add(new H4("Playing The Bug is a Lie"));
    add(
        new Paragraph(
            "The Bug is a Lie is a turn-based game. The manager always begins the game."));
    add(new Paragraph("On each turn, a player performs the following steps:"));
    add(new Paragraph("1. Deals with any stumbling block cards from other players."));
    add(
        new Paragraph(
            "2. Pops two cards (unless the player has a special ability) from the stack."));
    add(new Paragraph("3. Plays cards ( Abilities, Actions, Stumbling Blocks)"));
    add(
        new Paragraph(
            "4. At the end, a player may only have as many cards on the hand as he has mental health points left. All others must be dropped on the heap."));
    add(
        new Paragraph(
            "Playing action cards (step 3) is the main part of a turn. Some of the action cards may\n"
                + "be played for oneself, some for one other player, some for all other players; some"
                + "cards take effect immediately, some are placed in front of players as “stumbling"
                + "blocks” for their next turn. In general, a player may play as many cards as he likes"
                + "in this step (see below for restrictions)."
                + "Some action cards are dependent on the prestige of both the player who is playing"
                + "them and the player at whom they are targeted (see Bug card and PWND card)."
                + "This is indicated on the card. In general, in such cases, the card can only be played"
                + "if the prestige of the target player is the same or lower than the prestige of the"
                + "player who plays the card."));
    add(new Paragraph("The game ends when:"));
    add(
        new Paragraph(
            "• The manager is fired. If there are any characters other than the consultant"
                + "left, the evil code monkeys win (as one, including the ones already fired). If"
                + "only the consultant is left, he wins."));
    add(
        new Paragraph(
            "• All evil code monkeys and the consultant are fired. In this case, the"
                + "manager and the honest developers win (as one, including the ones already"
                + "fired)."));
    add(new Paragraph("The individual phases of a turn are as follows:"));
    add(
        new Paragraph(
            "Dealing with stumbling blocks: Other players may willingly or unwillingly place stumbling block cards on a player which he needs to take care of before really starting his turn. See section stumbling block cards below for details."));
    add(
        new Paragraph(
            "Popping cards from the stack:"
                + "The player takes the top two cards from the stack. Whenever the stack runs empty,"
                + "create a new stack by shuffling the heap."));
    add(new Paragraph("Playing Cards:"));
    add(
        new Paragraph(
            "In this phase, the players can play ability, action, and stumbling block cards."));
    add(
        new Paragraph(
            "⇒ The player places any ability cards he wants to use on the table. Note that"
                + "only one “Previous Job” card may be on the table per player."));
    add(
        new Paragraph(
            "⇒ The player plays action cards. Note that some cards for attacking someone"
                + "directly are dependent on prestige. This is indicated on the card. A typical"
                + "action is"));
    add(
        new Paragraph(
            "• “Attacking” another player with a Bug card, claiming that a bug was found in his code (only a player with a lower prestige can be attacked). He or she must defend himself with a lame excuse card (or specialability, see special cards) – otherwise he or she loses mental health. Note that only one Bug card may be used per turn (if not for specialability of a player)."));
    add(
        new Paragraph(
            "• The player may also play an arbitrary number of other action cards."
                + "These are detailed in the card section below."));
    add(
        new Paragraph(
            "⇒ The player plays stumbling block cards. Any stumbling block card may be"
                + "placed in front of an arbitrary player."
                + "Note that others may need to react to the actions of the currently active player,"
                + "for example defending themselves with a lame excuse card. However, they may not"
                + "play any cards except for “Solution” and “Lame Excuse” (their character card may"
                + "give them more options, though) while it is not their turn. During this phase, a"
                + "player may lose his last mental health point. In this case, he is fired and has to"
                + "leave the game. All of his cards are piled onto the heap (if not for special ability of"
                + "a player)."));
    add(
        new Paragraph(
            "Finishing the Turn: "
                + "At the end of a turn, a player may only have as many cards on the hand as he has"
                + "mental health points left. All others need to be dropped on the heap. Note that"
                + "between turns, a player may have more cards than mental health points."));

    // Role Cards
    add(new H4("Role Cards"));
    add(
        new Paragraph(
            "There are 7 role cards (1 Manager, 2 Honest Developers, 3 Evil Code\n"
                + "Monkeys, and 1 Consultant). These are used at the beginning of the game to give\n"
                + "everyone a role to play. Role cards are colored green . The role cards determine\n"
                + "the winning condition of the player:\n"));
    add(
        new Paragraph(
            "• Manager and Honest Developers win if all Evil Code Monkeys and the Con\u0002sultant are fired.\n"));
    add(new Paragraph("• Evil Code Monkeys win if the Manager is fired.\n"));
    add(
        new Paragraph(
            "• The Consultant wins if all others are fired, the Manager last (otherwise, the\n"
                + "Evil Code Monkeys win immediately)"));

    // Manager:
    add(
        getCardExplanation(
            "Manager",
            "The manager heads the software development team; he is\n"
                + "responsible for shipping the software and can rely on the honest developers\n"
                + "and (to a degree) the external consultant. His goal is to get the evil code\n"
                + "monkeys and the consultant fired so he can hire more honest developers.\n"));

    // HonestDeveloper:
    add(
        getCardExplanation(
            "HonestDeveloper",
            "They believe in good design and shipping software.\n"
                + "Thus, they help their manager getting the software finished and have the same\n"
                + "goals as he."));

    // EvilCodeMonkey:
    add(
        getCardExplanation(
            "EvilCodeMonkey",
            "They have no idea how to write good software\n"
                + "altogether, but they don’t want to be fired, so they try to fire the manager\n"
                + "instead, hoping for one of them to be promoted.\n"));

    // Consultant:
    add(
        getCardExplanation(
            "Consultant",
            "The consultant tries to get everyone else fired so he can take\n"
                + "over the department. He wins if no one else is left."));

    // Character Cards
    add(new H4("Character Cards"));
    add(
        new Paragraph(
            "There are 13 character cards, which give each player certain\n"
                + "special skills. Character cards are colored yellow. \n"
                + "Every player is dealt one of the yellow character cards. These cards define the\n"
                + "initial and maximum mental health points the player has. When playing “offline”, it\n"
                + "is recommended to keep a number of coins or pens indicating the current number of\n"
                + "health points. The current number of health points also indicates how many cards\n"
                + "the player may have in hand.\n"
                + "Furthermore, each card defines a special ability the player has. This ability stays\n"
                + "with him throughout the game and cannot be taken away."));

    // Tom Anderson:
    add(
        getCardExplanation(
            "TomAnderson", "If he loses mental health, he may take a card from the stack."));

    // Terry Weissman:
    add(
        getCardExplanation(
            "TerryWeissman", "With a chance of 0.25 a bug gets deflected for him.\n"));

    // Steve Jobs:
    add(
        getCardExplanation(
            "SteveJobs",
            "He gets a second chance, which means that whenever he has to\n"
                + "roll for a random number, he can roll a second time if he wants.\n"));

    // Steve Ballmer:
    add(
        getCardExplanation(
            "SteveBallmer",
            "He may use bugs as excuses and the other way round at any\n" + "time.\n"));

    // Mark Zuckerberg:
    add(
        getCardExplanation(
            "MarkZuckerberg",
            "If he loses mental health, he may take one card from the\n"
                + "causer (whether in hand or on the table, except character and role cards)."));

    // LinusTorvalds:
    add(
        getCardExplanation(
            "LinusTorvalds", "His Bug attacks can only be deflected by two excuses."));

    // Larry Page:
    add(getCardExplanation("LarryPage", " When somebody gets fired, he takes all of the cards."));

    // Larry Ellison:
    add(
        getCardExplanation(
            "LarryEllison",
            "He takes three cards instead of two cards from the stack, but\n"
                + "has to put one back onto the stack.\n"));

    // KonradZuse:
    add(getCardExplanation("KonradZuse", "He is seen with +1 prestige by everybody."));

    // KentBeck:
    add(
        getCardExplanation(
            "KentBeck",
            "He may, at any time, drop two cards to gain one mental health\n" + "point."));

    // Jeff Taylor:
    add(
        getCardExplanation(
            "JeffTaylor",
            "Whenever the player has no cards on his hand, he can take one\n"
                + "from the stack (it does not need to be his turn).\n"));

    // Holier than Thou:
    add(getCardExplanation("HolierThanThou", " He sees everyone with -1 prestige.\n"));

    // Bruce Schneier:
    add(
        getCardExplanation(
            "BruceSchneider",
            "He may report an arbitrary number of bugs – and not just\n" + "one.\n"));

    // Action Cards
    add(new H4("Action Cards"));
    add(
        new Paragraph(
            "Action Cards are played and have an immediate effect on another\n"
                + "player, or can be immediately used to deflect an attack performed by another player.\n"
                + "Action cards are colored black . They can be separated as follows:\n"
                + "25 Bug Reports\n"
                + "12 Lame Excuses\n"
                + "6 Solution Cards\n"
                + "20 Special Action Cards (4 Refactoring, 4 Pwned, 3 SI, 2 Standup Meeting,\n"
                + "2 Boring Meeting, 2 Coffee Machine, 1 LAN Party, 1 Red Bull Dispenser,\n"
                + "1 Heisenbug)\n"));

    // Boring Meeting:
    add(
        getCardExplanation(
            "BoringMeeting",
            "Everybody has to go to a boring meeting (and lose one\n"
                + "health point) – if they are not able to claim that they are working on a\n"
                + "bug (bug report card)."));

    // Bug Report
    /*add(getCardExplanation("BugReport", "A bug report against another player causes him to lose one\n" +
            "mental health point – if he has no lame excuse. He might actually lose a\n" +
            "health point but be able to compensate this by immediately “getting help”,\n" +
            "or by a special ability."));
    */
    // Class Not Found!
    add(getCardExplanation("ClassNotFound", "All information needed is written on the card"));

    // Code+Fix Session
    add(getCardExplanation("CodeFixSession", "All information needed is written on the card"));

    // Coffee
    add(
        getCardExplanation(
            "Coffee",
            "Playing this card allows the player to draw 2\n" + "new cards from the stack."));

    // Core Dump!
    add(getCardExplanation("CoreDump", "All information needed is written on the card"));

    // Customer hates UI!
    add(getCardExplanation("CustomerHatesUI", "All information needed is written on the card"));

    // Heisenbug
    add(getCardExplanation("Heisenbug", "All information needed is written on the card"));

    // I know regular expressions
    add(
        getCardExplanation(
            "IKnowRegularExpressions", "All information needed is written on the card"));

    // I´m not Responsible!
    add(getCardExplanation("ImNotResponsible", "All information needed is written on the card"));

    // I refactored your code. Away.
    add(
        getCardExplanation(
            "IRefactoredYourCodeAway", "All information needed is written on the card"));

    // It´s a Feature
    add(getCardExplanation("ItsAFeature", "All information needed is written on the card"));

    // Lame Excuse
    // add(getCardExplanation("LameExcuse", "All information needed is written on the card"));

    // LAN Party
    add(getCardExplanation("LANParty", "All information needed is written on the card"));

    // NullPointer
    add(getCardExplanation("NullPointer", "All information needed is written on the card"));

    // Off By One!
    add(getCardExplanation("OffByOne", "All information needed is written on the card"));

    // Personal Coffee Machine
    add(
        getCardExplanation(
            "PersonalCoffeeMachine", "All information needed is written on the card"));

    // Pwnd.
    add(getCardExplanation("Pwnd", "All information needed is written on the card"));

    // Red Bull Dispenser
    add(getCardExplanation("RedBullDispenser", "All information needed is written on the card"));

    // Standup Meeting
    add(getCardExplanation("StandupMeeting", "All information needed is written on the card"));

    // System Hangs!
    add(getCardExplanation("SystemHangs", "All information needed is written on the card"));

    // SystemIntegration
    add(getCardExplanation("SystemIntegration", "All information needed is written on the card"));

    // Works For Me!
    add(getCardExplanation("WorksForMe", "All information needed is written on the card"));

    // Ability Cards
    add(new H4("Ability Cards"));
    add(
        new Paragraph(
            "Ability cards are colored blue . They contain a specific ability,\n"
                + "which can be used by placing the card on the table in front of a player.\n"
                + "There are 13 ability cards. Two increase the player’s protection against bug reports;\n"
                + "the other 11 affect the (perceived) prestige of the player himself and his fellow\n"
                + "co-players.\n"
                + "2 Bug Delegation\n"
                + "1 NASA\n"
                + "2 Google\n"
                + "3 Microsoft\n"
                + "2 Accenture\n"
                + "2 Wears Tie at Work\n"
                + "1 Wears Sunglasses at Work"));

    // Accenture
    add(
        getCardExplanation(
            "Accenture",
            "In this case, prestige stays at zero,\n"
                + "but the player gains the ability to play arbitrarily many Bug cards per turn.\n"));

    // Bug Delegation
    add(
        getCardExplanation(
            "BugDelegation",
            "This card acts as a shield against bug reports. A player may\n"
                + "only place one Bug Delegation card in front of him. If a bug report comes in,\n"
                + "the player calculates a random number (you know the probability distribution\n"
                + "by now). If it lies under 0.25, he gets lucky and the bug gets delegated (i.d.\n"
                + "the card is dropped onto the heap). Otherwise, the bug report has to be dealt\n"
                + "with as usual.\n"));

    // Google
    add(getCardExplanation("Google", "All information needed is written on the card"));

    // Microsoft
    add(getCardExplanation("Microsoft", "All information needed is written on the card"));

    // NASA
    add(getCardExplanation("NASA", "All information needed is written on the card"));

    // Wears Sunglasses at Work
    add(
        getCardExplanation(
            "WearsSunglassesAtWork",
            " Wearing sunglasses is incredibly cool and instills\n"
                + "a lot of self-confidence. Thus, the player who places this card in front of him\n"
                + "may subtract one from the prestige of all other players. This only affects the\n"
                + "sunglass-wearing player.\n"));

    // Wears Tie at Work
    add(
        getCardExplanation(
            "WearsTieAtWork",
            "Wearing a tie commands respect in fellow co-workers. If\n"
                + "a player places this card in front of him, all others add one to his prestige."));
  }

  // Create card explanation view:
  private HorizontalLayout getCardExplanation(String cardName, String cardExplanation) {
    HorizontalLayout layoutBoringMeeting = new HorizontalLayout();
    Image boringMeetingImage = ImageBuilder.getCardImage(cardName, 112, 64);
    Paragraph boringMeetingExplanation = new Paragraph(cardExplanation);
    layoutBoringMeeting.add(boringMeetingImage, boringMeetingExplanation);
    return layoutBoringMeeting;
  }
}
