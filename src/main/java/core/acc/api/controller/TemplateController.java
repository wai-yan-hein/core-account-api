package core.acc.api.controller;

import core.acc.api.dao.COATemplateDao;
import core.acc.api.entity.COAKey;
import core.acc.api.entity.COATemplate;
import core.acc.api.entity.COATemplateKey;
import core.acc.api.service.COATemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
        return Flux.fromIterable(coaTemplateService.getChild(busId, coaCode));
    }

    @PostMapping(path = "/find-coa-template")
    public ResponseEntity<?> findCOA(@RequestBody COATemplateKey key) {
        return ResponseEntity.ok(coaTemplateService.findById(key));
    }

    @GetMapping(path = "get-coa-template-tree")
    public Flux<?> getCOATemplateTree(@RequestParam Integer busId, @RequestParam String coaCode) {
        return Flux.fromIterable(coaTemplateService.getCOATemplateTree(busId, coaCode)).onErrorResume(throwable -> Flux.empty());
    }
}
