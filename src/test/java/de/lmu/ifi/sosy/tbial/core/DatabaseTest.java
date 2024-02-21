// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.core;

import com.google.common.collect.ImmutableSet;
import com.google.common.truth.Truth;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.User.Type;
import de.lmu.ifi.sosy.tbial.core.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseTest {

  @Autowired UserRepository repository;

  @Test
  public void testDatabaseStoresCorrectPassword() {
    User user = new User("User", "testUser", "testUser", ImmutableSet.of(Type.USER), false, true);
    repository.save(user);
    Truth.assertThat(repository.findByUsername("testUser").getPassword()).isEqualTo("testUser");
    // will delete user but no other user with same attributes because of different id
    repository.delete(user);
  }
}
