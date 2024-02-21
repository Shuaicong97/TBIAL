// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.security;

import com.google.common.truth.Truth;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

public class CustomPasswordEncoderTest {

  @Property
  public void testPasswordsAreEncodedCorrectly(@ForAll String password) {
    Truth.assertThat(new CustomPasswordEncoder().encode(password)).isEqualTo(password);
  }
}
