package crv.manolin.auth;

public class JwtProvider {
    private static String secretKey;
    private static long validityInMilliseconds;

    protected static String generateToken(String username) {
        return "";
    }

    protected static boolean validateToken(String token) {
        return false;
    }
}
