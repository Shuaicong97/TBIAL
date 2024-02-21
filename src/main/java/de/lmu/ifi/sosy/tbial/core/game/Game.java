package de.lmu.ifi.sosy.tbial.core.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.lmu.ifi.sosy.tbial.core.DiscardedCard;
import de.lmu.ifi.sosy.tbial.core.DrawnCard;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.core.player.Player;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity(name = "Game")
public class Game {

  @Id
  @NotNull
  @NotEmpty
  @Column(unique = true)
  private String name;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Set<Player> players;

  private int maxPlayers;

  private boolean isLocked;
  private boolean isAttacked;

  @JsonIgnore private String password;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Card> deck;

  @OneToMany private List<Card> discardPile;

  private Status status;

  private String creator;

  private int currentTurnIndex;

  public Game(
      String name,
      int maxPlayers,
      boolean isLocked,
      String password,
      Status status,
      String creator) {
    this.name = name;
    this.maxPlayers = maxPlayers;
    this.isLocked = isLocked;
    this.password = password;
    this.status = status;
    this.creator = creator;
    this.players = new HashSet<>();
    this.deck = new Stack<>();
    this.discardPile = new Stack<>();
    this.isAttacked = false;
  }

  // needed for @Entity annotation
  public Game() {}

  public String getPassword() {
    return password;
  }

  public String getName() {
    return name;
  }

  public boolean isLocked() {
    return isLocked;
  }

  public int getMaxPlayers() {
    return maxPlayers;
  }

  public Set<Player> getPlayers() {
    return players;
  }

  public int getPlayersCount() {
    return players.size();
  }

  public String getCapacity() {
    return players.size() + " / " + maxPlayers;
  }

  public String getLocked() {
    return isLocked ? "password" : "open";
  }

  public Status getStatus() {
    return status;
  }

  public String getCreator() {
    return creator;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public int getCurrentTurnIndex() {
    return currentTurnIndex;
  }

  public void setCurrentTurnIndex(int currentTurnIndex) {
    this.currentTurnIndex = currentTurnIndex;
  }

  public boolean isAttacked() {
    return isAttacked;
  }

  public void setAttacked(boolean attacked) {
    isAttacked = attacked;
  }

  public void changeTurn() {
    List<Player> all = new ArrayList<>(players);
    this.currentTurnIndex = all.indexOf(all.get((currentTurnIndex + 1) % getPlayersCount()));
  }

  /**
   * Adds a player to the game.
   *
   * @param player The player to be added.
   */
  public void add(Player player) {
    players.add(player);
  }

  public void remove(Player player) {
    players.remove(player);
  }

  public boolean contains(String username) {
    return players.stream().anyMatch(player -> player.getUsername().equals(username));
  }

  public boolean canBeJoined() {
    return getPlayersCount() < getMaxPlayers();
  }

  @Override
  public String toString() {
    return "Game{"
        + "name='"
        + name
        + '\''
        + ", maxPlayers="
        + maxPlayers
        + ", isLocked="
        + isLocked
        + ", password='"
        + password
        + '\''
        + '}';
  }

  public Optional<Card> drawFromDeck(Player trigger) {
    // Return the card on the top
    if (!this.deck.isEmpty()) {
      Card card = this.deck.get(this.deck.size() - 1);
      this.deck.remove(card);
      // Notify views that a card was drawn
      Broadcaster.broadcast(new ViewData<>(new DrawnCard(card, trigger), ViewData.Type.DRAW_CARD));
      return Optional.of(card);
    }
    return Optional.empty();
  }

  public void discardToPile(Card card, Player trigger) {
    // trigger can only play the solution or excuse cards
    if (isAttacked) {
      if (trigger.getNumOfBeingAttacked() == 1
          && (card.getName().equals("Coffee")
              || card.getName().equals("CodeFixSession")
              || card.getName().equals("IKnowRegularExpressions")
              || card.getName().equals("WorksForMe")
              || card.getName().equals("ItsAFeature")
              || card.getName().equals("ImNotResponsible"))) {
        if (!this.discardPile.isEmpty()) this.discardPile.add(this.discardPile.size() - 1, card);
        else this.discardPile.add(card);
        // Notify views that a card was discarded. Send all discarded cards from before
        Broadcaster.broadcast(
            new ViewData<>(new DiscardedCard(card, trigger), ViewData.Type.DISCARD_CARD));
        Broadcaster.broadcast(
            new ViewData<>(new DiscardedCard(card, trigger), ViewData.Type.DISCARD_PILE));
        trigger.setNumOfBeingAttacked(0);
        System.out.println(trigger.getUsername() + " plays a defense card.");
      }
    } else {
      // Put the card always on the top of the discard pile
      if (!this.discardPile.isEmpty()) this.discardPile.add(this.discardPile.size() - 1, card);
      else this.discardPile.add(card);
      // Notify views that a card was discarded. Send all discarded cards from before
      Broadcaster.broadcast(
          new ViewData<>(new DiscardedCard(card, trigger), ViewData.Type.DISCARD_CARD));
      Broadcaster.broadcast(
          new ViewData<>(new DiscardedCard(card, trigger), ViewData.Type.DISCARD_PILE));
      System.out.println(trigger.getUsername() + " plays a card.");
    }
  }

  public void setDeck(List<Card> deck) {
    this.deck = deck;
  }

  public List<Card> getDeck() {
    return deck;
  }

  public List<Card> getDiscardPile() {
    return discardPile;
  }

  public void setDiscardPile(List<Card> discardPile) {
    this.discardPile = discardPile;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Game)) return false;
    Game game = (Game) o;
    return name.equals(game.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
