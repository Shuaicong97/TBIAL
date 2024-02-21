// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.security;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import de.lmu.ifi.sosy.tbial.views.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Configures all security settings. */
// enables custom security settings
@EnableWebSecurity
// indicates Vaadin that the class should be treated as configuration, i.e., processed by container
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

  public static final String LOGOUT_URL = "/";

  /**
   * Defines how passwords are encoded in the database.
   *
   * @return the custom password encoder {@link CustomPasswordEncoder}
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new CustomPasswordEncoder();
  }

  /**
   * Configures the security settings for HTTP
   *
   * @param security instance of {@code HttpSecurity} class that will be configured
   * @throws Exception throws exception if the configuration is invalid or provokes conflicts.
   */
  @Override
  protected void configure(HttpSecurity security) throws Exception {
    super.configure(security);
    setLoginView(security, LoginView.class, LOGOUT_URL);
  }

  /**
   * Configures the security settings for Web
   *
   * @param security instance of {@code WebSecurity} class that will be configured
   * @throws Exception throws exception if the configuration is invalid or provokes conflicts.
   */
  @Override
  public void configure(WebSecurity security) throws Exception {
    super.configure(security);
    security.ignoring().antMatchers("/images/*.png");
  }
}
