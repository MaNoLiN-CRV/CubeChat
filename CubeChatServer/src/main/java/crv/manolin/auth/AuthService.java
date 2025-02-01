package crv.manolin.auth;

import crv.manolin.entities.User;

public class AuthService {

    public static String authenticate(String username, String password) {
        // TODO: implement authentication
        return JwtProvider.generateToken(username);
    }

    public static User validateToken(String token) {
        // TODO: implement token validation
        return null;
    }
}
