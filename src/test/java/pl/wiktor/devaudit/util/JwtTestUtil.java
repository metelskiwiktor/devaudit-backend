package pl.wiktor.devaudit.util;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;

import java.time.Instant;
import java.util.*;

public class JwtTestUtil {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String subject;
        private final List<String> roles = new ArrayList<>();
        private final Map<String, Object> extraClaims = new HashMap<>();

        public Builder subject(String sub) {
            this.subject = sub;
            return this;
        }

        public Builder role(String role) {
            this.roles.add(role);
            return this;
        }

        public Builder claim(String name, Object value) {
            this.extraClaims.put(name, value);
            return this;
        }

        public String buildBearer() {
            JWTClaimsSet.Builder cb = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(Date.from(Instant.now()));
            if (!roles.isEmpty()) {
                cb.claim("realm_access", Map.of("roles", roles));
            }
            extraClaims.forEach(cb::claim);
            PlainJWT plain = new PlainJWT(cb.build());
            return "Bearer " + plain.serialize();
        }
    }
}
