package pl.wiktor.devaudit.config;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public JwtDecoder jwtDecoder() {
        return token -> {
            try {
                JWT parsed = JWTParser.parse(token);
                JWTClaimsSet claims = parsed.getJWTClaimsSet();

                Jwt.Builder builder = Jwt.withTokenValue(token)
                        .headers(h -> h.putAll(parsed.getHeader().toJSONObject()));

                for (Map.Entry<String, Object> e : claims.getClaims().entrySet()) {
                    Object value = e.getValue();
                    if (value instanceof Date) {
                        builder.claim(e.getKey(), ((Date) value).toInstant());
                    } else {
                        builder.claim(e.getKey(), value);
                    }
                }

                return builder.build();
            } catch (ParseException ex) {
                throw new JwtException("An error occurred while attempting to decode the Jwt: " + ex.getMessage(), ex);
            }
        };
    }
}
