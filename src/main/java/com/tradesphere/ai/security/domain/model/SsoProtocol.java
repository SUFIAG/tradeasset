package com.tradesphere.ai.security.domain.model;

/**
 * SSO Authentication Protocols
 */
public enum SsoProtocol {
    /**
     * SAML 2.0 Protocol
     */
    SAML,
    
    /**
     * OpenID Connect
     */
    OIDC,
    
    /**
     * OAuth 2.0
     */
    OAUTH2,
    
    /**
     * LDAP/Active Directory
     */
    LDAP
}

