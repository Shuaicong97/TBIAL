package de.lmu.ifi.sosy.tbial.core.cards;

public enum Character {
  LINUS_TORVALDS("LinusTorvalds"),
  TOM_ANDERSON("TomAnderson"),
  STEVE_BALLMER("SteveBallmer"),
  KENT_BECK("KentBeck"),
  LARRY_ELLISON("LarryEllison"),
  HOLIER_THAN_THOU("HolierThanThou"),
  STEVE_JOBS("SteveJobs"),
  LARRY_PAGE("LarryPage"),
  BRUCE_SCHNEIDER("BruceSchneider"),
  JEFF_TAYLOR("JeffTaylor"),
  TERRY_WEISSMAN("TerryWeissman"),
  MARK_ZUCKERBERG("MarkZuckerberg"),
  KONRAD_ZUSE("KonradZuse");

  private final String name;

  Character(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
