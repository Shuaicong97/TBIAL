package de.lmu.ifi.sosy.tbial.core.game;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class GameServiceImpl implements GameService {

  Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
  private List<Game> games;
  private final GameRepository repository;

  public GameServiceImpl(@Autowired GameRepository repository) {
    this.repository = repository;
    games = repository.findAll(); // This service is a singleton and this happens at init.
    logger.info("All games returned from the database.");
  }

  public void save(Game game) {
    repository.save(game);
    logger.info("Game " + game.getName() + " successfully saved to database.");
    games = repository.findAll(); // Update object in memory.
  }

  public Game findBy(String gameName) {
    logger.info("Fetching game " + gameName + " from the database.");
    return repository.findByName(gameName);
  }

  public List<Game> getGames() {
    logger.info("All games returned from memory.");
    return games;
  }

  public void delete(Game game) {
    repository.delete(game);
    logger.info("Game " + game.getName() + " successfully deleted from database.");
    games = repository.findAll();
  }
}
