package de.lmu.ifi.sosy.tbial.core.chats;

import java.time.Instant;
import java.time.ZoneOffset;

public class ChatMessage {
  private String sender;
  private String message;
  private Instant date;

  // chatID of in game or lobby chat: unique name of the game
  // chatID of general chat: "general"
  private String chatID;

  public ChatMessage(String sender, String message, String chatID) {
    this.sender = sender;
    this.message = message;
    this.chatID = chatID;
    this.date = Instant.now();
  }

  public ChatMessage() {}

  public String getSender() {
    return sender;
  }

  public String getMessage() {
    return message;
  }

  public String getDate() {
    int minutes = date.atZone(ZoneOffset.systemDefault()).getMinute();
    String minutesString = String.valueOf(minutes);
    if (minutes < 10) {
      minutesString = "0" + minutesString;
    }

    int hours = date.atZone(ZoneOffset.systemDefault()).getHour();
    String hoursString = String.valueOf(hours);
    if (hours < 10) {
      hoursString = "0" + hoursString;
    }

    return hoursString + ":" + minutesString;
  }

  public String getChatID() {
    return chatID;
  }
}
