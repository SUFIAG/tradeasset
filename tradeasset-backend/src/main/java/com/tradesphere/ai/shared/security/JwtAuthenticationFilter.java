package com.tradesphere.ai.shared.security;

import com.tradesphere.ai.shared.multitenancy.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the security context from a verified bearer access token, and from nothing else
 * (REQ-SEC-002). Identity headers such as X-Tenant-ID or X-User-Id are ignored.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Request attribute set when the presented token was expired, so the 401 can say so. */
    public static final String EXPIRED_TOKEN_ATTRIBUTE = JwtAuthenticationFilter.class.getName() + ".expired";

    private final JwtCodec jwt;

    public JwtAuthenticationFilter(JwtCodec jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            JwtCodec.Verification verification = jwt.verifyAccessToken(header.substring(7).trim());
            if (verification.user().isPresent()) {
                AuthenticatedUser user = verification.user().get();
                List<GrantedAuthority> authorities = new ArrayList<>();
                user.roles().forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
                user.permissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                TenantContext.setTenantId(user.tenantId());
            } else if (verification.expired()) {
                request.setAttribute(EXPIRED_TOKEN_ATTRIBUTE, Boolean.TRUE);
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
