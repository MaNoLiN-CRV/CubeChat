package crv.manolin.auth;

public class JwtProvider {
    private String secretKey;
    private long validityInMilliseconds;

    public String generateToken(String username) {
        return "";
    }

    public boolean validateToken(String token) {
        return false;
    }
}
