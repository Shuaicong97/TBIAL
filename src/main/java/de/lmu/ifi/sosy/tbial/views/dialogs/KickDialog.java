package de.lmu.ifi.sosy.tbial.views.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.game.GameViewModel;
import de.lmu.ifi.sosy.tbial.utils.Helpers;
import de.lmu.ifi.sosy.tbial.views.Broadcaster;

public class KickDialog extends Dialog {
  public KickDialog(GameViewModel gameViewModel, String username) {
    getElement().setAttribute("aria-label", "Kick Player");
    H2 headline = new H2("Kick Player");
    headline
        .getStyle()
        .set("margin", "var(--lumo-space-m) 0 0 0")
        .set("font-size", "1.5em")
        .set("font-weight", "bold");
    String text = "Are you sure you want to kick user '" + username + "' out of this lobby?";
    Paragraph message = new Paragraph(text);

    Button cancel = new Button("Cancel", e -> close());
    Button kick = new Button("Kick");
    kick.addClickListener(
        e -> {
          gameViewModel.kickPlayer(username);
          kick.getUI().ifPresent(ui -> ui.navigate(Helpers.LOBBY));
          Broadcaster.broadcast(new ViewData<>(gameViewModel.getAllGames(), ViewData.Type.GAMES));
          close();
        });

    kick.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
    HorizontalLayout buttonLayout = new HorizontalLayout(cancel, kick);
    buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

    VerticalLayout layout = new VerticalLayout(headline, message, buttonLayout);
    layout.setAlignItems(FlexComponent.Alignment.STRETCH);
    layout.getStyle().set("width", "300px").set("max-width", "100%");
    add(layout);
  }
}
