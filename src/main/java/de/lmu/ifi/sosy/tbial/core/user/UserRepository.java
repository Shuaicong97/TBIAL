// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.core.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

  /**
   * Hibernate declaration of a database query by username.
   *
   * @param username return user with this username
   * @return user with username {@code pUsername}
   */
  User findByUsername(String username);
}
