package de.lmu.ifi.sosy.tbial.core.player;

import de.lmu.ifi.sosy.tbial.core.cards.Card;
import de.lmu.ifi.sosy.tbial.core.cards.Character;
import de.lmu.ifi.sosy.tbial.core.cards.Role;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity(name = "Player")
public class Player {

  @Id
  @NotNull
  @NotEmpty
  @Column(unique = true)
  private String username;

  @OneToMany(fetch = FetchType.EAGER)
  private List<Card> cards;

  private Role role;

  private Character character;

  private Type type;
  private int prestige;
  private int healthPoints;

  private int numOfBeingAttacked;
  private int numOfBugPlayedPerTurn;

  public Player() {}

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public int getPrestige() {
    return prestige;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getHealthPoints() {
    return healthPoints;
  }

  public void setPrestige(int prestige) {
    this.prestige = prestige;
  }

  public void setHealthPoints(int healthPoints) {
    this.healthPoints = healthPoints;
  }

  public String getUsername() {
    return username;
  }

  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }

  public Role getRole() {
    return role;
  }

  public Character getCharacter() {
    return character;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public int getNumOfBeingAttacked() {
    return numOfBeingAttacked;
  }

  public void setNumOfBeingAttacked(int numOfBeingAttacked) {
    this.numOfBeingAttacked = numOfBeingAttacked;
  }

  public int getNumOfBugPlayedPerTurn() {
    return numOfBugPlayedPerTurn;
  }

  public void setNumOfBugPlayedPerTurn(int numOfBugPlayedPerTurn) {
    this.numOfBugPlayedPerTurn = numOfBugPlayedPerTurn;
  }

  /**
   * A card is being played. It's therefore removed from the players hand. This method sends a
   * broadcast msg to all views interested in this event.
   *
   * @param card The card which the player plays
   * @return The card object which was removed
   */
  public Card removeFromHand(Card card) {
    this.cards.remove(card);
    return card;
  }

  /**
   * A card is being drawn. It's therefore added to the players hand.
   *
   * @param card The card which the player picks up
   */
  public void addToHand(Card card) {
    this.cards.add(card);
  }

  public Optional<Card> findByName(String cardName) {
    return cards.stream().filter(card -> card.getName().equals(cardName)).findFirst();
  }

  public void setCharacter(Character character) {
    this.character = character;
  }

  public boolean isDead() {
    return this.healthPoints <= 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Player)) return false;
    Player player = (Player) o;
    return username.equals(player.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
