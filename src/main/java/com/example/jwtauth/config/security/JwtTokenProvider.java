package com.example.jwtauth.config.security;


import com.example.jwtauth.payload.JwtAuthenticationResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.util.Date;

/**
 * The following utility class will be used for generating a JWT after a user logs in successfully, and validating the JWT sent in the Authorization header of the requests
 * The utility class reads the JWT secret and expiration time from application.properties.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String USER_SECRET = "userSecret";

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    private final UserDetailsService userService;

    @Autowired
    public JwtTokenProvider(@Qualifier ( "UserService" ) UserDetailsService userService) {
        this.userService = userService;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        userPrincipal.setAuthorities (authentication.getAuthorities());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setClaims(buildUserClaims(userPrincipal))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        userPrincipal.setAuthorities (authentication.getAuthorities());
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .setClaims(buildUserClaims(userPrincipal))
                .setIssuedAt(new Date())
                .compact();
    }

    Jws<Claims> validateJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    private Jws<Claims> validateJwtRefreshToken(String token) {
        JwtParser parser = Jwts.parser().setSigningKey(jwtSecret);
        Jws<Claims> claims = parser.parseClaimsJws(token);
        UserPrincipal  user = (UserPrincipal) userService.loadUserByUsername((String)claims.getBody().get("login"));
        System.out.println(user.getSecretToken());
        return parser.require(USER_SECRET, user.getSecretToken()).parseClaimsJws(token);

    }

    boolean validateTokenLog(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    private Claims buildUserClaims(UserPrincipal  user) {
        Claims claims = new DefaultClaims();
        claims.setSubject(String.valueOf(user.getId()));
        claims.put("login", user.getUsername());
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("roles", String.join(",", AuthorityUtils.authorityListToSet(user.getAuthorities())));
        claims.put(USER_SECRET, user.getSecretToken());
        return claims;
    }
    /**
     * @return newly generated access token or nothing, if the refresh token is not valid
     */
    public JwtAuthenticationResponse refreshAccessToken(String refreshToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jws<Claims> claims = validateJwtRefreshToken(refreshToken);
        return new JwtAuthenticationResponse(generateToken(authentication),refreshToken);
    }

}


