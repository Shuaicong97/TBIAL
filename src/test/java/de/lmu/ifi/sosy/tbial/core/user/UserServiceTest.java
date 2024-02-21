package de.lmu.ifi.sosy.tbial.core.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
  @Mock private UserRepository mockRepository;
  @Mock private User mockUser;
  @Mock private List<User> mockUsers;
  private UserServiceImpl service;

  @BeforeEach
  public void setUp() {
    // Set up for service init
    Mockito.when(mockRepository.findAll()).thenReturn(mockUsers);
    service = new UserServiceImpl(mockRepository); // first games query during init
  }

  @Test
  public void whenSave_thenRepositorySaves_thenUsersUpdated() {
    Mockito.when(mockRepository.save(mockUser)).thenReturn(mockUser);

    service.save(mockUser); // second users query during save

    Mockito.verify(mockRepository, Mockito.atMostOnce()).save(mockUser);
    Mockito.verify(mockRepository, Mockito.atMost(2)).findAll();
  }

  @Test
  public void whenFindBy_thenSucceeds() {
    Mockito.when(mockUser.getUsername()).thenReturn("Tiffany");
    Mockito.when(mockRepository.findByUsername("Tiffany")).thenReturn(mockUser);

    User result = service.findBy("Tiffany");

    Mockito.verify(mockRepository, Mockito.atMostOnce()).findByUsername("Tiffany");
    assertEquals("Tiffany", result.getUsername());
  }

  @Test
  public void beforeSave_whenGetUsers_thenFromMemory() {
    List<User> users = service.getUsers();

    assertEquals(0, users.size());
    Mockito.verify(mockRepository, Mockito.atMostOnce()).findAll(); // Because of service init
  }

  @Test
  public void afterSave_whenGetUsers_thenFromDatabase() {
    Mockito.when(mockRepository.save(mockUser)).thenReturn(mockUser);

    service.save(mockUser); // second games query during save
    service.getUsers(); // doesn't query the database

    Mockito.verify(mockRepository, Mockito.atMost(2)).findAll();
  }
}
