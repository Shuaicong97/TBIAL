package de.lmu.ifi.sosy.tbial.core.game;

import java.util.List;

public interface GameService {

  void save(Game game);

  Game findBy(String gameName);

  List<Game> getGames();

  void delete(Game game);
}
