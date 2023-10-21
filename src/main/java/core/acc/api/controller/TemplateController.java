package core.acc.api.controller;

import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
import core.acc.api.service.COATemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/template")
public class TemplateController {
    @Autowired
    private COATemplateService coaTemplateService;

    @PostMapping(path = "/saveCOA")
    public Mono<?> saveCOATemplate(@RequestBody COATemplate obj) {
        return Mono.justOrEmpty(coaTemplateService.save(obj));
    }

    @GetMapping(path = "/getCOAChild")
    public Flux<?> getCOAChild(@RequestParam Integer busId, @RequestParam String coaCode) {
        return Flux.fromIterable(coaTemplateService.getCOAChild(busId, coaCode));
    }

    @PostMapping(path = "/findCOATemplate")
    public Mono<?> findCOATemplate(@RequestBody COATemplateKey key) {
        return Mono.just(coaTemplateService.findById(key));
    }

    @GetMapping(path = "/getCOATemplateTree")
    public Flux<?> getCOATemplateTree(@RequestParam Integer busId) {
        return Flux.fromIterable(coaTemplateService.getCOATemplateTree(busId)).onErrorResume(throwable -> Flux.empty());
    }
    @GetMapping(path = "/getCOATemplate")
    public Flux<?> getCOATemplate(@RequestParam Integer busId) {
        return Flux.fromIterable(coaTemplateService.getCOATemplate(busId)).onErrorResume(throwable -> Flux.empty());
    }
}
