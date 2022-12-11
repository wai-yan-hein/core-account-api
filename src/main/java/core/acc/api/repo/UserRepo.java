package core.acc.api.repo;

import core.acc.api.model.PropertyKey;
import core.acc.api.model.SystemProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class UserRepo {
    private final HashMap<String, String> hmKey = new HashMap<>();
    int min = 1;
    @Autowired
    private WebClient userApi;
    private List<String> location;
    public SystemProperty findProperty(String key, String compCode) {
        PropertyKey p = new PropertyKey();
        p.setPropKey(key);
        p.setCompCode(compCode);
        Mono<SystemProperty> result = userApi.post()
                .uri("/user/find-system-property")
                .body(Mono.just(p), PropertyKey.class)
                .retrieve()
                .bodyToMono(SystemProperty.class);
        return result.block(Duration.ofMinutes(min));
    }

    public String getProperty(String key) {
        if (hmKey.isEmpty()) {
            Mono<ResponseEntity<List<SystemProperty>>> result = userApi.get()
                    .uri(builder -> builder.path("/user/get-system-property")
                            .queryParam("compCode", "-")
                            .build())
                    .retrieve().toEntityList(SystemProperty.class);
            ResponseEntity<List<SystemProperty>> block = result.block();
            if (block != null) {
                List<SystemProperty> list = block.getBody();
                if (list != null) {
                    for (SystemProperty s : list) {
                        hmKey.put(s.getKey().getPropKey(), s.getPropValue());
                    }
                }
            }
        }
        return hmKey.get(key);
    }

}
