package de.lmu.ifi.sosy.tbial.core.cards;

public enum Role {
  MANAGER("Manager"),
  EVIL_CODE_MONKEY("EvilCodeMonkey"),
  CONSULTANT("Consultant"),
  HONEST_DEVELOPER("HonestDeveloper");

  private final String name;

  Role(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
