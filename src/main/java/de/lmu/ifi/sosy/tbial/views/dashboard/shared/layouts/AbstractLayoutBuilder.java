package de.lmu.ifi.sosy.tbial.views.dashboard.shared.layouts;

import com.vaadin.flow.component.Component;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLayoutBuilder {

  protected static final String UNIT = "px";

  protected int height = 0;

  protected int width = 0;

  protected boolean sizeUndefined = false;

  protected List<String> cssClasses = new ArrayList<>();

  protected List<Component> components = new ArrayList<>();
}
