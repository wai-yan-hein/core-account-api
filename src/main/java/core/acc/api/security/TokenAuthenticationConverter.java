package core.acc.api.security;


import core.acc.api.common.Util1;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author duc-d
 */
public class TokenAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String BEARER = "Bearer ";
    private static final Predicate<String> matchBearerLength = authValue -> authValue.length() > BEARER.length();
    private static final Function<String, String> isolateBearerValue = authValue -> authValue.substring(BEARER.length(), authValue.length());

    private final JwtService jwtService;

    public TokenAuthenticationConverter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange).map(SecurityUtils::getTokenFromRequest)
                .filter(Objects::nonNull).filter(matchBearerLength)
                .map(isolateBearerValue)
                .filter(token -> !Util1.isNullOrEmpty(token)).map(jwtService::getAuthentication)
                .filter(Objects::nonNull);
    }
}
