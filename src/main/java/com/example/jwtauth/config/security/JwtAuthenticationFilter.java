package com.example.jwtauth.config.security;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;



import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  JwtAuthenticationFilter
 * reads JWT authentication token from the Authorization header of all the requests
 * validates the token
 * loads the user details associated with that token.
 * Sets the user details in Spring Security’s SecurityContext. Spring Security uses the user details to perform authorization checks.
 * We can also access the user details stored in the SecurityContext in our controllers to perform our business logic.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String USER_SECRET = "userSecret";

    @Autowired
    private JwtTokenProvider tokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt) && tokenProvider.validateTokenLog(jwt)) {
                Jws<Claims> claims = tokenProvider.validateJwtToken(jwt);
                Authentication authentication;
                authentication = getAuthentication(claims);
                System.out.println(authentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    private Authentication getAuthentication(Jws<Claims> token) {
        UserPrincipal user = new UserPrincipal(token.getBody().get("id", Long.class), token.getBody().get("login", String.class), token.getBody().get("email", String.class), token.getBody().get(USER_SECRET, String.class));
        return new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(token.getBody().get("roles", String.class)));
    }
}
