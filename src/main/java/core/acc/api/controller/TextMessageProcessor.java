package core.acc.api.controller;

import core.acc.api.entity.Gl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
@Slf4j
public class TextMessageProcessor {
    private final List<Consumer<Gl>> listeners = new CopyOnWriteArrayList<>();

    public void register(Consumer<Gl> listener) {
        listeners.add(listener);
        log.info("New listener add : " + listener.toString());
    }

    public void process(Gl gl) {
        log.info("Process Message");
        listeners.forEach(c -> c.accept(gl));
    }
}