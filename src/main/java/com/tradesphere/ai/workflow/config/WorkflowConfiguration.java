package com.tradesphere.ai.workflow.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for workflow module
 */
@Configuration
@ComponentScan(basePackages = "com.tradesphere.ai.workflow")
@EnableScheduling
public class WorkflowConfiguration {
    
    // Bean definitions can be added here if needed
    // For now, component scanning handles everything
}


