package core.acc.api.controller;

import core.acc.api.GlProcessor;
import core.acc.api.entity.Gl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/gl")
public class GlController {
    @Autowired
    private GlProcessor processor;

    @PostMapping("send")
    public String send(@RequestBody Gl gl) {
        processor.process(gl);
        return "Done";
    }

    @GetMapping(path = "/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Gl> receive() {
        return Flux.create(sink -> processor.register(sink::next));
    }
}
