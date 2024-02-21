package de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class HorizontalLayoutBuilder extends AbstractLayoutBuilder {

  public HorizontalLayoutBuilder withHeight(int height) {
    super.height = height;
    return this;
  }

  public HorizontalLayoutBuilder withWidth(int width) {
    super.width = width;
    return this;
  }

  public HorizontalLayoutBuilder withSizeUndefined() {
    super.sizeUndefined = true;
    return this;
  }

  public HorizontalLayoutBuilder withCssClass(String cssClass) {
    super.cssClasses.add(cssClass);
    return this;
  }

  public HorizontalLayoutBuilder withComponent(Component component) {
    super.components.add(component);
    return this;
  }

  public HorizontalLayout build() {
    HorizontalLayout horizontalLayout = new HorizontalLayout();

    if (components.size() > 0) {
      horizontalLayout.add(
          components.toArray(Component[]::new)); // new Component[10] da aber Größe nicht bekannt
    }

    if (cssClasses.size() > 0) {
      horizontalLayout.addClassNames(cssClasses.toArray(String[]::new));
    }

    if (width > 0) {
      horizontalLayout.setWidth(width + UNIT);
    }

    if (height > 0) {
      horizontalLayout.setHeight(height + UNIT);
    }

    if (sizeUndefined) {
      horizontalLayout.setSizeUndefined();
    }

    return horizontalLayout;
  }
}
