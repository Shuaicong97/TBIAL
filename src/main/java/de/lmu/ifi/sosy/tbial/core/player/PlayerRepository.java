package de.lmu.ifi.sosy.tbial.core.player;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, String> {

  List<Player> findAll();

  List<Player> findByUsername(String username);
}
