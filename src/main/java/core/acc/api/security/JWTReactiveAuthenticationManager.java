package core.acc.api.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

/**
 * @author duc-d
 */
@Slf4j
@AllArgsConstructor
public class JWTReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        String token = (String) authentication.getCredentials();
        return Mono.just(jwtService.isTokenValid(token)).flatMap(valid -> {
            if (valid) {
                return Mono.just(jwtService.getAuthentication(token));
            }
            return Mono.empty();
        });
    }


}