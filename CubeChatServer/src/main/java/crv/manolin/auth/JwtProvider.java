package crv.manolin.auth;

import crv.manolin.config.Configuration;
import crv.manolin.debug.DebugCenter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;

public class JwtProvider {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Configuration configInstance = Configuration.getInstance();
    private static final long VALIDITY_IN_MILLISECONDS = configInstance.getJwtExpiration();

    /**
     * Generates a JWT token for the given username
     *
     * @param username The username to encode in the token
     * @return A signed JWT token as a string
     */
    protected static String generateToken(String username) {
        long now = System.currentTimeMillis();
        Date validity = new Date(now + VALIDITY_IN_MILLISECONDS);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(validity)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Validates a given JWT token
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    protected static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            DebugCenter.error("Token expired: " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            DebugCenter.error("Invalid token signature: " + e.getMessage());
            return false;
        } catch (Exception e) {
            DebugCenter.error("Token validation error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extracts the username from a valid JWT token
     *
     * @param token The JWT token to extract username from
     * @return The username if the token is valid, null otherwise
     */
    protected static String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            DebugCenter.error("Error extracting username: " + e.getMessage());
            return null;
        }
    }

}