package com.tradesphere.ai.shared.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Source address and user agent of the current HTTP request, for audit (REQ-REL-003). */
public final class RequestMetadata {

    private RequestMetadata() {
    }

    public static String clientIp() {
        HttpServletRequest request = currentRequest();
        return request == null ? null : clientIp(request);
    }

    public static String clientIp(HttpServletRequest request) {
        // Behind a trusted proxy configure server.forward-headers-strategy so getRemoteAddr is correct.
        return request.getRemoteAddr();
    }

    public static String userAgent() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return null;
        }
        String ua = request.getHeader("User-Agent");
        return ua == null ? null : (ua.length() > 400 ? ua.substring(0, 400) : ua);
    }

    private static HttpServletRequest currentRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return sra.getRequest();
        }
        return null;
    }
}
