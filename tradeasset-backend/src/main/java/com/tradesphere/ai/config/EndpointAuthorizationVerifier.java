package com.tradesphere.ai.config;

import com.tradesphere.ai.shared.security.PublicEndpoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Fails startup when a controller method of this application has neither an explicit
 * {@code @PreAuthorize} nor {@code @PublicEndpoint}. A new endpoint without an authorisation rule
 * therefore cannot ship (deny by default).
 */
@Component
public class EndpointAuthorizationVerifier implements ApplicationListener<ContextRefreshedEvent> {

    private final RequestMappingHandlerMapping mapping;

    public EndpointAuthorizationVerifier(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<String> offenders = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mapping.getHandlerMethods().entrySet()) {
            HandlerMethod handler = entry.getValue();
            Class<?> type = handler.getBeanType();
            if (!type.getPackageName().startsWith("com.tradesphere.ai")) {
                continue;
            }
            boolean authorised = AnnotatedElementUtils.hasAnnotation(handler.getMethod(), PreAuthorize.class)
                    || AnnotatedElementUtils.hasAnnotation(type, PreAuthorize.class)
                    || AnnotatedElementUtils.hasAnnotation(handler.getMethod(), PublicEndpoint.class)
                    || AnnotatedElementUtils.hasAnnotation(type, PublicEndpoint.class);
            if (!authorised) {
                offenders.add(type.getSimpleName() + "#" + handler.getMethod().getName() + " " + entry.getKey());
            }
        }
        if (!offenders.isEmpty()) {
            throw new IllegalStateException("Endpoints without an explicit authorisation rule: " + offenders);
        }
    }
}
