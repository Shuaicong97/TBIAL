package de.lmu.ifi.sosy.tbial.core.user;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public class UserServiceImpl implements UserService {

  Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
  private List<User> users;
  private final UserRepository repository;

  public UserServiceImpl(@Autowired UserRepository repository) {
    this.repository = repository;
    users = repository.findAll(); // This service is a singleton and this happens at init.
    logger.info("All users returned from the database.");
  }

  public void save(User user) {
    repository.save(user);
    logger.info("User " + user.getName() + " successfully saved to database.");
    users = repository.findAll(); // Update object in memory.
  }

  public List<User> getUsers() {
    logger.info("All users returned from memory.");
    return users;
  }

  public User findBy(String gameName) {
    return repository.findByUsername(gameName);
  }
}
