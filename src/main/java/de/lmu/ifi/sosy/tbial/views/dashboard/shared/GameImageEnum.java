package de.lmu.ifi.sosy.tbial.views.dashboard.shared;

public enum GameImageEnum {
  BRAIN("brain.PNG", "brain.png", "Brain"),
  CROWN("crown.PNG", "crown.png", "Crown");

  private final String name;

  private final String fileName;

  private final String alt;

  GameImageEnum(String name, String fileName, String alt) {
    this.name = name;
    this.fileName = fileName;
    this.alt = alt;
  }

  public String getName() {
    return name;
  }

  public String getFileName() {
    return fileName;
  }

  public String getAlt() {
    return alt;
  }
}
