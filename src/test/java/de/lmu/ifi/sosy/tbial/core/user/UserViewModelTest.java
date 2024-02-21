package de.lmu.ifi.sosy.tbial.core.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class UserViewModelTest {
  @Mock private UserService mockService;
  private UserViewModel viewModel;
  @Mock private SecurityContext mockContext;
  @Mock private Authentication mockAuthentication;
  private User signedInUser;

  @BeforeEach
  public void setUp() {
    viewModel = new UserViewModel(mockService);
    signedInUser =
        new User(
            "Jack Sparrow", "jack_sparrow", "black_pearl", Collections.emptySet(), true, false);

    Mockito.lenient().when(mockContext.getAuthentication()).thenReturn(mockAuthentication);
    Mockito.lenient().when(mockAuthentication.getName()).thenReturn(signedInUser.getUsername());
    SecurityContextHolder.setContext(mockContext);
  }

  @Test
  public void whenGetCurrentUser_thenSuccessful() {
    Mockito.when(mockService.findBy(Mockito.any())).thenReturn(signedInUser);

    User result = viewModel.getCurrentUser(); // jack_sparrow is the currently signed-in user

    assertEquals(result.getUsername(), signedInUser.getUsername());
    Mockito.verify(mockService, Mockito.times(1)).findBy(signedInUser.getUsername());
  }

  @Test
  public void whenGetOnlineUsers_thenSuccessful() {
    List<User> users = createTestUsers();
    Mockito.when(mockService.getUsers()).thenReturn(users);

    List<User> result = viewModel.getOnlineUsers(); // Only john_doe and jack_sparrow are online

    assertFalse(result.isEmpty());
    assertTrue(findByName(users, "jack_sparrow").isPresent());
    assertTrue(findByName(users, "john_doe").isPresent());
  }

  @Test
  public void whenFindBy_thenSuccessful() {
    viewModel.findBy(Mockito.any());

    Mockito.verify(mockService, Mockito.times(1)).findBy(Mockito.any());
  }

  @Test
  public void whenSetOnline_thenSuccessful() {
    Mockito.when(mockService.findBy(Mockito.any())).thenReturn(signedInUser);
    signedInUser.setOnline(false); // jack_sparrow goes offline to drink alcohol with Martin

    viewModel.setOnline(true); // jack_sparrow is the signed-in user

    assertTrue(signedInUser.isOnline());
    Mockito.verify(mockService, Mockito.times(1)).save(signedInUser);
  }

  @Test
  public void whenSetAvailable_thenSuccessful() {
    Mockito.when(mockService.findBy(Mockito.any())).thenReturn(signedInUser);
    signedInUser.setAvailable(false); // jack_sparrow is not available any more sorry Martin

    viewModel.setAvailable(true); // jack_sparrow is the signed-in user

    assertTrue(signedInUser.isAvailable());
    Mockito.verify(mockService, Mockito.times(1)).save(signedInUser);
  }

  private List<User> createTestUsers() {
    List<User> users = new ArrayList<>();
    users.add(new User("John Doe", "john_doe", "secret", Collections.emptySet(), true, true));
    users.add(
        new User(
            "Jack Sparrow", "jack_sparrow", "black_pearl", Collections.emptySet(), true, false));
    users.add(
        new User(
            "George Clooney", "george_clooney", "star_wars", Collections.emptySet(), false, false));
    users.add(
        new User("Hermione Granger", "hermione", "magic", Collections.emptySet(), false, true));
    users.add(
        new User(
            "Martin Product Owner",
            "the_critical_one",
            "you_shall_not_pass",
            Collections.emptySet(),
            true,
            true));

    return users;
  }

  private Optional<User> findByName(List<User> users, String name) {
    return users.stream().filter(user -> user.getUsername().equals(name)).findFirst();
  }
}
