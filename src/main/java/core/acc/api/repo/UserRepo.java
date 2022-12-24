package core.acc.api.repo;

import core.acc.api.model.DepartmentUser;
import core.acc.api.model.PropertyKey;
import core.acc.api.model.SystemProperty;
import core.acc.api.service.DepartmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserRepo {
    private final HashMap<String, String> hmKey = new HashMap<>();
    private List<DepartmentUser> listDept;
    @Autowired
    private DepartmentService departmentService;

    int min = 1;
    @Autowired
    private WebClient userApi;
    private String deptCode;
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
    public List<DepartmentUser> getDepartment() {
        if (listDept == null) {
            try {
                Mono<ResponseEntity<List<DepartmentUser>>> result = userApi.get()
                        .uri(builder -> builder.path("/user/get-department")
                                .build())
                        .retrieve().toEntityList(DepartmentUser.class);
                listDept = Objects.requireNonNull(result.block(Duration.ofMinutes(min))).getBody();
            } catch (Exception e) {
                log.error("getDepartment : " + e.getMessage());
            }
        }
        return listDept;
    }
    public String getDepCode() {
        if (deptCode == null) {
            List<DepartmentUser> list = getDepartment();
            if (list != null) {
                Integer deptId = list.get(0).getDeptId();
                return departmentService.getDepartment(deptId);

            }
        }
        return deptCode;
    }
    public String getProperty(String key) {
        if (hmKey.isEmpty()) {
            try {
                Mono<ResponseEntity<List<SystemProperty>>> result = userApi.get()
                        .uri(builder -> builder.path("/user/get-system-property")
                                .queryParam("compCode", "-")
                                .build())
                        .retrieve().toEntityList(SystemProperty.class);
                ResponseEntity<List<SystemProperty>> block = result.block(Duration.ofMinutes(1));
                if (block != null) {
                    List<SystemProperty> list = block.getBody();
                    if (list != null) {
                        for (SystemProperty s : list) {
                            hmKey.put(s.getKey().getPropKey(), s.getPropValue());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("getProperty : " + e.getMessage());
            }
        }
        return hmKey.get(key);
    }

}
