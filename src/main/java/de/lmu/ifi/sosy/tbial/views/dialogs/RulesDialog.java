package de.lmu.ifi.sosy.tbial.views.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.lmu.ifi.sosy.tbial.views.RulesView;

public class RulesDialog extends Dialog {

  public RulesDialog() {
    getElement().setAttribute("aria-label", "Invitation");
    H2 headline = new H2("Rules");
    headline
        .getStyle()
        .set("margin", "var(--lumo-space-m) 0 0 0")
        .set("font-size", "1.5em")
        .set("font-weight", "bold");
    VerticalLayout dialogLayout =
        new VerticalLayout(
            new Button("Close", e -> close()),
            headline,
            new RulesView(),
            new Button("Close", e -> close()));
    dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
    add(dialogLayout);
  }
}
