package core.acc.api.controller;


import core.acc.api.message.Message;
import core.acc.api.message.MessageProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageProcessor processor;

    @PostMapping("/send")
    public Mono<?> send(@RequestBody Message gl) {
        processor.process(gl);
        return Mono.just("sent");
    }

    @GetMapping(path = "/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> receive(@RequestParam String messageId) {
        return Flux.create(sink -> processor.register(messageId, sink::next));
    }
}
