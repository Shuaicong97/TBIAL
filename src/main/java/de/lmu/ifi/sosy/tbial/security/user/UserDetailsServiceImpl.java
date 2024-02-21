// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.security.user;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import de.lmu.ifi.sosy.tbial.core.user.User;
import de.lmu.ifi.sosy.tbial.core.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Allow queries to the user repository. Spring creates this service automatically and injects all
 * necessary parameters.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(@Autowired UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Authorize user with associated roles.
   *
   * @param user current user
   * @return All roles encoded in granted authorities
   */
  private static ImmutableList<SimpleGrantedAuthority> getAuthorities(User user) {
    return FluentIterable.from(user.getTypes())
        .transform(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
        .toList();
  }

  /**
   * Find details for user with given username. The details provide necessary information to
   * guarantee correct authentication.
   *
   * @param username the username for the user to authenticate
   * @return the credentials for the given username
   * @throws UsernameNotFoundException thrown if no user with given username exists.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("No user present with username: " + username);
    } else {
      return new org.springframework.security.core.userdetails.User(
          user.getUsername(), user.getPassword(), getAuthorities(user));
    }
  }
}
