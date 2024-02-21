package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.lmu.ifi.sosy.tbial.core.chats.ChatMessage;

public class ChatView extends VerticalLayout {

  TextField messageField = new TextField();

  public ChatView(String chatID, String username, MessageList messageList, String chatName) {

    H4 chatTitle = new H4(chatName);
    add(chatTitle);
    expand(messageList);

    HorizontalLayout layout = new HorizontalLayout();
    messageField.addKeyPressListener(Key.ENTER, e -> sendMessage(username, chatID));
    Button sendButton = new Button("Send");
    sendButton.addClickListener(click -> sendMessage(username, chatID));
    messageField.focus();

    layout.add(messageField, sendButton);
    layout.setWidth("100%");
    layout.expand(messageField);
    VerticalLayout chatPanel = new VerticalLayout();
    Div chatDiv = new Div();
    chatDiv.add(messageList);
    chatPanel.setWidth("550px");
    chatDiv.setHeight("400px");
    chatPanel.add(chatDiv);
    Scroller chatScroller = new Scroller(chatPanel);
    chatScroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
    chatScroller.getStyle().set("border", "1px solid var(--lumo-contrast-20pct)");
    add(chatScroller, layout);
  }

  private void sendMessage(String username, String chatID) {
    if (messageField.isEmpty()) {
      Notification.show("Please enter your message.", 3500, Notification.Position.BOTTOM_STRETCH);
    } else {
      ChatMessage msg = new ChatMessage(username, messageField.getValue(), chatID);
      Broadcaster.postMessage(msg);
      messageField.clear();
      messageField.focus();
    }
  }
}
