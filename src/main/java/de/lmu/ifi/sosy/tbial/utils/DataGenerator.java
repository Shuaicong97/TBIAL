package de.lmu.ifi.sosy.tbial.utils;

import com.google.common.collect.ImmutableSet;
import com.vaadin.flow.spring.annotation.SpringComponent;
import de.lmu.ifi.sosy.tbial.core.game.GameRepository;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.User.Type;
import de.lmu.ifi.sosy.tbial.core.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

/** This class generates dummy data to get started. */
@SpringComponent
public class DataGenerator {

  @Bean
  public CommandLineRunner loadData(UserRepository userRepository, GameRepository gameRepository) {
    return args -> {
      User johny =
          new User("Johny Knoxville", "user", "user", ImmutableSet.of(Type.USER), false, true);
      userRepository.save(johny);

      User girl = new User("Girl Power", "girl", "girl", ImmutableSet.of(Type.USER), false, true);
      userRepository.save(girl);

      User marvel =
          new User("Captain Marvel", "marvel", "marvel", ImmutableSet.of(Type.USER), false, true);
      userRepository.save(marvel);

      User bruce =
          new User("Bruce Willis", "bruce", "bruce", ImmutableSet.of(Type.USER), false, true);
      userRepository.save(bruce);

      User admin =
          new User(
              "Administrator",
              "admin",
              "admin",
              ImmutableSet.of(Type.USER, Type.ADMIN),
              false,
              true);
      userRepository.save(admin);

      User user3 = new User("David", "user3", "user3", ImmutableSet.of(Type.USER), false, true);
      userRepository.save(user3);

      User user4 = new User("Jane", "user4", "user", ImmutableSet.of(Type.USER), false, true);
      userRepository.save(user4);
    };
  }
}
