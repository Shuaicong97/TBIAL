package de.lmu.ifi.sosy.tbial.views.game;

public class CreateGameInput {
  // String name, int playersCount, boolean isLocked, String password
  private String name;
  private Integer maxPlayers;
  private Boolean isLocked;
  private String password;

  public CreateGameInput() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(int playersCount) {
    this.maxPlayers = playersCount;
  }

  public boolean isLocked() {
    return isLocked;
  }

  public void setLocked(boolean locked) {
    isLocked = locked;
  }

  public String getPassword() {
    return password == null ? "" : password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
