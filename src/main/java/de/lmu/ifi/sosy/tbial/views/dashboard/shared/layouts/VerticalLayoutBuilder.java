package de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class VerticalLayoutBuilder extends AbstractLayoutBuilder {

  public VerticalLayoutBuilder withHeight(int height) {
    super.height = height;
    return this;
  }

  public VerticalLayoutBuilder withWidth(int width) {
    super.width = width;
    return this;
  }

  public VerticalLayoutBuilder withSizeUndefined() {
    super.sizeUndefined = true;
    return this;
  }

  public VerticalLayoutBuilder withCssClass(String cssClass) {
    super.cssClasses.add(cssClass);
    return this;
  }

  public VerticalLayoutBuilder withComponent(Component component) {
    super.components.add(component);
    return this;
  }

  public VerticalLayout build() {
    VerticalLayout verticalLayout = new VerticalLayout();

    if (components.size() > 0) {
      verticalLayout.add(
          components.toArray(Component[]::new)); // new Component[10] da aber Größe nicht bekannt
    }

    if (cssClasses.size() > 0) {
      verticalLayout.addClassNames(cssClasses.toArray(String[]::new));
    }

    if (width > 0) {
      verticalLayout.setWidth(width + UNIT);
    }

    if (height > 0) {
      verticalLayout.setHeight(height + UNIT);
    }

    if (sizeUndefined) {
      verticalLayout.setSizeUndefined();
    }

    return verticalLayout;
  }
}
