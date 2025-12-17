package service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import dto.UserBean;
import entity.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import repository.UserRepository;

import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    TokenService tokenService;

    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

//    public String login(UserBean bean) {
//        Optional<User> userOpt = userRepository.findByUsername(bean.getUserName());
//        if(userOpt.isEmpty()) return null;
//
//        User user = userOpt.get();
//
//        if(verifyPassword(user.getPassword(), bean.getPassword().toCharArray())) {
//            return tokenService.generateToken(user,"USER");
//        }
//
//        return null;
//    }

    public Map<String, String> login(UserBean bean) {
        Optional<User> userOpt = userRepository.findByUsername(bean.getUserName());
        if(userOpt.isEmpty()) return null;

        User user = userOpt.get();

        if(verifyPassword(user.getPassword(), bean.getPassword().toCharArray())) {
            String accessToken = tokenService.generateToken(Optional.of(user), "USER");         // หมดอายุสั้น ~10 นาที
            String refreshToken = tokenService.refreshAccessToken(accessToken);         // หมดอายุยาว ~30 วัน

            // return ทั้งคู่เป็น JSON
            return Map.of(
                    "accessToken", accessToken,
                    "refreshToken", refreshToken
            );
        }

        return null;
    }

    public static String hashPassword(String plainPassword) {
        // ส่งเป็น char array เพราะ String มันอยู่ใน heap มันจะไม่โดนลบออกในทันที -> เสี่ยง leak
        char[] passwordChars = plainPassword.toCharArray();
        try {
            return argon2.hash(3, 65536, 1, passwordChars);
        } finally {
            argon2.wipeArray(passwordChars); // ลบออกจาก memory
        }
    }

    public static boolean verifyPassword(String hash, char[] plainPassword) {
        try {
            return argon2.verify(hash, plainPassword);
        } finally {
            argon2.wipeArray(plainPassword);
        }
    }

}
