package core.acc.api.controller;

import core.acc.api.entity.Gl;
import core.acc.api.model.Notification;
import core.acc.api.service.NotificationProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/notification")
public class NotificationController {
    @Autowired
    private NotificationProcessor processor;

    @GetMapping(path = "/getNotification", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notification> getNotification() {
        return Flux.create(sink -> processor.register(sink::next));
    }

    @PostMapping(path = "/pushNotification")
    public void pushNotification(@RequestBody Notification notification) {
        processor.process(notification);
    }
}
