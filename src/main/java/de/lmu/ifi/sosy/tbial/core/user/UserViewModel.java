package de.lmu.ifi.sosy.tbial.core.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserViewModel {
  private final UserService userService;

  private String inviterUsername;
  private String inviteeUsername;

  public UserViewModel(@Autowired UserService userService) {
    this.userService = userService;
  }

  public User getCurrentUser() throws UsernameNotFoundException {
    SecurityContext context = SecurityContextHolder.getContext();
    Optional<User> user =
        Optional.ofNullable(context.getAuthentication())
            .filter(authentication -> !(authentication instanceof AnonymousAuthenticationToken))
            .map(authentication -> userService.findBy(authentication.getName()));
    return user.orElseThrow();
  }

  public List<User> getOnlineUsers() {
    List<User> onlineUsers = new ArrayList<>();
    for (User user : userService.getUsers()) {
      if (user.isOnline()) {
        onlineUsers.add(user);
      }
    }
    return onlineUsers;
  }

  public User findBy(String username) {
    return userService.findBy(username);
  }

  public void setOnline(boolean isOnline) {
    User user = getCurrentUser();
    user.setOnline(isOnline);
    userService.save(user);
  }

  public List<User> getAvailableUsers() {
    List<User> availableUsers = new ArrayList<>();
    for (User user : userService.getUsers()) {
      if (user.isAvailable()) {
        availableUsers.add(user);
      }
    }
    return availableUsers;
  }

  public void setAvailable(boolean isAvailable) {
    User user = getCurrentUser();
    user.setAvailable(isAvailable);
    userService.save(user);
  }

  public String getInviterUsername() {
    return inviterUsername;
  }

  public void setInviterUsername(String inviterUsername) {
    this.inviterUsername = inviterUsername;
  }

  public String getInviteeUsername() {
    return inviteeUsername;
  }

  public void setInviteeUsername(String inviteeUsername) {
    this.inviteeUsername = inviteeUsername;
  }
}
