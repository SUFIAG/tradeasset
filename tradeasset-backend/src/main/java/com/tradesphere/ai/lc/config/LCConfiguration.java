package com.tradesphere.ai.lc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for LC Module
 */
@Configuration
@ComponentScan(basePackages = "com.tradesphere.ai.lc")
public class LCConfiguration {
    // Spring will automatically scan and wire all components
}

