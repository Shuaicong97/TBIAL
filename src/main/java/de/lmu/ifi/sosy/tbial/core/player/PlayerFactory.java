package de.lmu.ifi.sosy.tbial.core.player;

import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerFactory {

  Logger logger = LoggerFactory.getLogger(PlayerRepository.class);
  @Autowired private PlayerRepository playerRepository;

  public Player createPlayer(String username, Type type) {
    Player player = new Player();
    player.setUsername(username);
    player.setPrestige(0);
    player.setHealthPoints(4);
    player.setType(type);
    player.setNumOfBeingAttacked(0);
    player.setNumOfBugPlayedPerTurn(0);
    player.setCards(Collections.emptyList());

    playerRepository.save(player);
    logger.info(
        "New player "
            + player.getUsername()
            + " with type "
            + player.getType()
            + " successfully saved in database.");
    return player;
  }
}
