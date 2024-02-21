package de.lmu.ifi.sosy.tbial.views;

import com.vaadin.flow.shared.Registration;
import de.lmu.ifi.sosy.tbial.core.ViewData;
import de.lmu.ifi.sosy.tbial.core.chats.ChatMessage;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Broadcaster {
  static Executor executor = Executors.newSingleThreadExecutor();

  static LinkedList<Consumer<ViewData>> listeners = new LinkedList<>();

  public static void postMessage(ChatMessage message) {
    broadcast(new ViewData<>(message, ViewData.Type.MESSAGE));
  }

  public static synchronized Registration register(Consumer<ViewData> listener) {
    listeners.add(listener);

    return () -> {
      synchronized (Broadcaster.class) {
        listeners.remove(listener);
      }
    };
  }

  public static synchronized void broadcast(ViewData data) {
    for (Consumer<ViewData> listener : listeners) {
      executor.execute(() -> listener.accept(data));
    }
  }
}
