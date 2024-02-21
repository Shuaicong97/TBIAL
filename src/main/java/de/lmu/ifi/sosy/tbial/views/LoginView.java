// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.lmu.ifi.sosy.tbial.utils.Helpers;

@PageTitle("Login")
@Route(value = Helpers.LOGIN)
public class LoginView extends LoginOverlay {

  private static final long serialVersionUID = -4873888008578779549L;

  /** International LoginView querying and checking username and password. */
  public LoginView() {
    setAction("login");

    LoginI18n i18n = LoginI18n.createDefault();
    i18n.setHeader(new LoginI18n.Header());
    i18n.getHeader().setTitle("TBIAL");
    i18n.getHeader().setDescription("Login - Please enter your credentials.");
    i18n.setAdditionalInformation(null);
    setI18n(i18n);

    // users cannot reset password for now
    setForgotPasswordButtonVisible(false);
    setOpened(true);
  }
}
