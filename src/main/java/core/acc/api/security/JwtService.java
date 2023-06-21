package core.acc.api.security;

import core.acc.api.common.Util1;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {
    private static final String AUTHORITIES_KEY = "auth";


    public AuthenticationResponse generateToken(UserDetails userDetails) {
        long expiration = Duration.ofDays(30).toMillis();
        String token = buildToken(userDetails, expiration);
        return AuthenticationResponse.builder().accessToken(token).accessTokenExpired(System.currentTimeMillis() + expiration).build();
    }

    public AuthenticationResponse generateRefreshToken(UserDetails userDetails) {
        long expiration = Duration.ofDays(60).toMillis();
        String token = buildToken(userDetails, expiration);
        return AuthenticationResponse.builder().refreshToken(token).refreshTokenExpired(System.currentTimeMillis() + expiration).build();
    }

    private String buildToken(UserDetails userDetails, long expiration) {
        String authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        return Jwts.builder().setSubject(userDetails.getUsername()).claim(AUTHORITIES_KEY, authorities).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expiration)).signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    public boolean isTokenValid(String token) {
        return containsTwoPeriods(token) && !isTokenExpired(token);
    }

    public static boolean containsTwoPeriods(String token) {
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            log.error("invalid token" + e.getMessage());
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        if (Util1.isNullOrEmpty(token) || !isTokenValid(token)) {
            throw new BadCredentialsException("Invalid token");
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
