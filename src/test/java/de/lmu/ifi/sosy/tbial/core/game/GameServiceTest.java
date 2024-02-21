package de.lmu.ifi.sosy.tbial.core.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GameServiceTest {

  @Mock private GameRepository mockRepository;
  @Mock private Game game;
  @Mock private List<Game> games;
  private GameServiceImpl service;

  @BeforeEach
  public void setUp() {
    // Set up for service init
    Mockito.when(mockRepository.findAll()).thenReturn(games);
    service = new GameServiceImpl(mockRepository); // first games query during init
  }

  @Test
  public void whenSave_thenRepositorySaves_thenGamesUpdated() {
    Mockito.when(mockRepository.save(game)).thenReturn(game);

    service.save(game); // second games query during save

    Mockito.verify(mockRepository, Mockito.atMostOnce()).save(game);
    Mockito.verify(mockRepository, Mockito.atMost(2)).findAll();
  }

  @Test
  public void whenFindBy_thenSucceeds() {
    Mockito.when(game.getName()).thenReturn("Starbucks");
    Mockito.when(mockRepository.findByName("Starbucks")).thenReturn(game);

    Game result = service.findBy("Starbucks");

    Mockito.verify(mockRepository, Mockito.atMostOnce()).findByName("Starbucks");
    assertEquals("Starbucks", result.getName());
  }

  @Test
  public void beforeSave_whenGetGames_thenFromMemory() {
    List<Game> games = service.getGames();

    assertEquals(0, games.size());
    Mockito.verify(mockRepository, Mockito.atMostOnce()).findAll(); // Because of service init
  }

  @Test
  public void afterSave_whenGetGames_thenFromDatabase() {
    Mockito.when(mockRepository.save(game)).thenReturn(game);

    service.save(game); // second games query during save
    service.getGames(); // doesn't query the database

    Mockito.verify(mockRepository, Mockito.atMost(2)).findAll();
  }

  @Test
  public void whenDelete_thenRepositoryDeletes_thenGamesUpdated() {
    Game game1 = mock(Game.class);
    service.delete(game1);
    Mockito.verify(mockRepository).delete(game1);
  }
}
