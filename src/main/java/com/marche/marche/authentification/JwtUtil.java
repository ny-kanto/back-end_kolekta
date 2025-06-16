package com.marche.marche.authentification;

import io.jsonwebtoken.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.marche.marche.modele.Utilisateur;
import com.marche.marche.repository.PersonneRepository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final String secret_key = "mysecretkey";
    private long accessTokenValidity = 60 * 60;

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    // Utilisation d'une carte pour stocker les tokens et leurs dates d'expiration
    private final Map<String, Date> blacklistedTokens = new HashMap<>();
    @Autowired
    private PersonneRepository pr;

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }

    public long getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public void setAccessTokenValidity(long accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    public void addTokenToBlacklist(String token, Date expirationDate) {
        blacklistedTokens.put(token, expirationDate);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    public void removeExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
    }

    public String createToken(Utilisateur user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("id", user.getId());
        claims.put("role", user.getAdmin(pr));
        claims.put("firstName", user.getEmail());
        claims.put("password", user.getPassword());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.MINUTES.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    public Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                if (isTokenBlacklisted(token)) {
                    throw new JwtException("Token is blacklisted.");
                }
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            // Ajouter une vérification supplémentaire pour les tokens blacklistés
            if (isTokenBlacklisted(claims.getId())) {
                return false;
            }
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public static int getUserId(Claims claims) {
        return claims.get("id", Integer.class);
    }

    // private List<String> getRoles(Claims claims) {
    // return (List<String>) claims.get("roles");
    // }

}
