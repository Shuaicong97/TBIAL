package de.lmu.ifi.sosy.tbial.core.game;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, String> {

  /**
   * Hibernate declaration of a database query by name.
   *
   * @param name return game with this gameName
   * @return Game with name {@code name}
   */
  Game findByName(String name);

  List<Game> findAll();
}
