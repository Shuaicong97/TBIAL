package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import javax.annotation.security.PermitAll;

@PageTitle("Rules")
@Route(value = Helpers.RULES, layout = MainLayout.class)
@PermitAll
public class RulesTab extends VerticalLayout {

  private static final long serialVersionUID = 6437642691282922374L;

  public RulesTab() {
    add(new RulesView());
  }
}
