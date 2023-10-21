package core.acc.api.service;

import core.acc.api.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
@Slf4j
public class NotificationProcessor {
    private final List<Consumer<Notification>> listeners = new CopyOnWriteArrayList<>();

    public void register(Consumer<Notification> listener) {
        listeners.add(listener);
        log.info("New listener add : " + listener.toString());
    }

    public void process(Notification notification) {
        log.info("Process Message");
        listeners.forEach(c -> c.accept(notification));
    }
}