// SPDX-FileCopyrightText: 2022 Dirk Beyer <https://www.sosy-lab.org>
//
// All rights reserved.

package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import javax.annotation.security.PermitAll;

@PageTitle("About")
@Route(value = Helpers.ABOUT, layout = MainLayout.class)
@PermitAll
public class AboutView extends VerticalLayout {

  private static final long serialVersionUID = 6437333691282922374L;

  /** Provides information about the game and the developers */
  public AboutView() {
    setSpacing(false);

    add(new H2("Lego Team:"));

    add(new Paragraph("Janka, Christine, Shuaicong, Yashar & Bogdan"));
    setAlignItems(Alignment.CENTER);
  }
}
