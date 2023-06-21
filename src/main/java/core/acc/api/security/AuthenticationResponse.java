package core.acc.api.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {

    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpired;
    private Long refreshTokenExpired;
    private String message;
    private Integer macId;
}
