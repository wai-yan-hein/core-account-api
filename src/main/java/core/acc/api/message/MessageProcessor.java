package core.acc.api.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class MessageProcessor {
    private final Map<String, Consumer<Message>> listeners = new HashMap<>();

    public void register(String id, Consumer<Message> listener) {
        listeners.put(id, listener);
    }

    public void process(Message message) {
        listeners.values().forEach(listener -> listener.accept(message));
    }
}