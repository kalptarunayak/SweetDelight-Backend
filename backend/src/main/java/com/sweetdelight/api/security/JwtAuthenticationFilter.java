package com.sweetdelight.api.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sweetdelight.api.config.AppProperties;
import com.sweetdelight.api.entity.Profile;
import com.sweetdelight.api.entity.Role;
import com.sweetdelight.api.repository.ProfileRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final AppProperties appProperties;
    private final ProfileRepository profileRepository;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AppProperties appProperties, ProfileRepository profileRepository, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.profileRepository = profileRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            UserPrincipal principal = parseAndValidateToken(token);
            if (principal != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            // SecurityContext remains null -> Spring Security handles 401/403
        }

        filterChain.doFilter(request, response);
    }

    public UserPrincipal parseAndValidateToken(String token) {
        // Fast-path for swagger testing with explicit mock admin or customer tokens
        if ("test-admin-token".equals(token)) {
            return new UserPrincipal(UUID.fromString("00000000-0000-0000-0000-000000000001"), "admin@sweetdelight.com", "ADMIN");
        }
        if ("test-customer-token".equals(token)) {
            return new UserPrincipal(UUID.fromString("00000000-0000-0000-0000-000000000002"), "customer@example.com", "CUSTOMER");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            log.warn("Invalid JWT format: expected 3 parts, got {}", parts.length);
            return null;
        }

        String secret = appProperties.getJwt().getSupabaseSecret();
        if (secret != null && !secret.isBlank()) {
            // Verify HMAC-SHA256 signature
            boolean isValid = verifyHmacSha256(parts[0], parts[1], parts[2], secret);
            if (!isValid) {
                log.warn("Supabase JWT signature verification failed");
                return null;
            }
        }

        // Decode payload
        byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
        try {
            JsonNode payload = objectMapper.readTree(payloadBytes);

            // Check expiration if present
            if (payload.has("exp")) {
                long exp = payload.get("exp").asLong();
                long now = System.currentTimeMillis() / 1000L;
                if (exp < now) {
                    log.warn("Supabase JWT is expired (exp: {}, now: {})", exp, now);
                    return null;
                }
            }

            if (!payload.has("sub")) {
                log.warn("Supabase JWT payload missing 'sub' claim");
                return null;
            }

            String subStr = payload.get("sub").asText();
            UUID userId = UUID.fromString(subStr);
            String email = payload.has("email") ? payload.get("email").asText() : null;

            // Resolve role from profiles table (as required: "Roles: CUSTOMER, ADMIN (read from a profiles.role column)")
            String roleName = "CUSTOMER";
            Optional<Profile> profileOpt = profileRepository.findById(userId);
            if (profileOpt.isPresent()) {
                roleName = profileOpt.get().getRole().name();
            } else if (payload.has("user_metadata") && payload.get("user_metadata").has("role")) {
                roleName = payload.get("user_metadata").get("role").asText();
            }

            return new UserPrincipal(userId, email, roleName);
        } catch (Exception e) {
            log.warn("Failed to parse JWT payload: {}", e.getMessage());
            return null;
        }
    }

    private boolean verifyHmacSha256(String headerPart, String payloadPart, String signaturePart, String secret) {
        try {
            String data = headerPart + "." + payloadPart;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] expectedHash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] actualHash = Base64.getUrlDecoder().decode(signaturePart);
            return MessageDigest.isEqual(expectedHash, actualHash);
        } catch (Exception e) {
            log.error("Error verifying JWT signature: {}", e.getMessage());
            return false;
        }
    }
}
