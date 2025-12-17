package service;

import dto.UserBean;
import entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.enterprise.context.ApplicationScoped;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Inject;
import lombok.Value;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import repository.UserRepository;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@ApplicationScoped
public class TokenService {

    @Inject
    UserRepository userRepository;

    @ConfigProperty(name = "jwt.secret")
    String secretKey;

    public String generateToken(Optional<User>  beanOpt , String roleUser) {

        User bean = beanOpt.orElseThrow(() -> new IllegalArgumentException("User cannot be null"));

        Date now = new Date();
        // หมดเวลาในอีก 10 นาที
        Date exp = new Date(System.currentTimeMillis() + 60_000);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());



        return Jwts.builder()
                .setSubject(bean.getId().toString())
                .setIssuedAt(now)
                .claim("role", roleUser)
                .claim("name", bean.getUserName())
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public String refreshAccessToken(String refreshToken) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken);

        String role = claims.getBody().get("role", String.class);

        Optional<User> userOpt = userRepository.findByUsername(claims.getBody().get("name", String.class));

        return generateToken(userOpt, role);
    }
}
