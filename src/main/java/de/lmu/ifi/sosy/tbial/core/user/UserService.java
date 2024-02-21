package de.lmu.ifi.sosy.tbial.core.user;

import java.util.List;

public interface UserService {

  void save(User user);

  List<User> getUsers();

  User findBy(String username);
}
