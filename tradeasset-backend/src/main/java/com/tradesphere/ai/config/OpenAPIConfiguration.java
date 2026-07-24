package com.tradesphere.ai.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 * Accessible at: /swagger-ui.html
 */
@Configuration
public class OpenAPIConfiguration {

    @Value("${spring.application.name:TradeSphere AI}")
    private String applicationName;

    @Value("${api.version:1.0.0}")
    private String apiVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .components(securityComponents())
                .addSecurityItem(securityRequirement());
    }

    private Info apiInfo() {
        return new Info()
                .title(applicationName + " API")
                .version(apiVersion)
                .description("""
                        Enterprise Trade Finance Platform API
                        
                        This API provides comprehensive trade finance operations including:
                        - Customer Management (KYC, Credit Limits, Risk Assessment)
                        - Letter of Credit (Application, Approval, Issuance, Document Review)
                        - Document Management with AI-powered extraction
                        - Compliance Screening (Sanctions, AML, PEP)
                        - Payment Processing
                        - Audit Trail and Reporting
                        
                        ## Authentication
                        All API endpoints require authentication via JWT token.
                        Use the /api/v1/auth/login endpoint to obtain a token.
                        
                        ## Multi-Tenancy
                        All requests must include X-Tenant-ID header with valid tenant UUID.
                        
                        ## Pagination
                        List endpoints support pagination with page, size, and sort parameters.
                        Default page size is 20, maximum is 100.
                        
                        ## Error Handling
                        API uses standard HTTP status codes:
                        - 200: Success
                        - 201: Created
                        - 400: Bad Request (validation error)
                        - 401: Unauthorized
                        - 403: Forbidden
                        - 404: Not Found
                        - 409: Conflict (business rule violation)
                        - 500: Internal Server Error
                        """)
                .contact(new Contact()
                        .name("TradeSphere Support")
                        .email("support@tradesphere.ai")
                        .url("https://tradesphere.ai"))
                .license(new License()
                        .name("Proprietary")
                        .url("https://tradesphere.ai/license"));
    }

    private List<Server> serverList() {
        return List.of(
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development"),
                new Server()
                        .url("https://api-dev.tradesphere.ai")
                        .description("Development Environment"),
                new Server()
                        .url("https://api-staging.tradesphere.ai")
                        .description("Staging Environment"),
                new Server()
                        .url("https://api.tradesphere.ai")
                        .description("Production Environment")
        );
    }

    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token obtained from /api/v1/auth/login"))
                .addSecuritySchemes("tenant-id", new SecurityScheme()
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .name("X-Tenant-ID")
                        .description("Tenant UUID for multi-tenancy"));
    }

    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement()
                .addList("bearer-jwt")
                .addList("tenant-id");
    }
}

