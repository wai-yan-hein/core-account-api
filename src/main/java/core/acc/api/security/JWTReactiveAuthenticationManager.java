package core.acc.api.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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