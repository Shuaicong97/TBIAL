// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/** Simple password encoder performing noop */
public class CustomPasswordEncoder implements PasswordEncoder {

  /**
   * Do not encode passwords
   *
   * @param rawPassword The chosen password
   * @return The same password
   */
  @Override
  public String encode(CharSequence rawPassword) {
    return (String) rawPassword;
  }

  /**
   * The raw password matches the encoded password if they are equal
   *
   * @param rawPassword The password a user provides
   * @param encodedPassword The actual password the user set while registering
   * @return whether the passwords match
   */
  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return rawPassword.equals(encodedPassword);
  }
}
