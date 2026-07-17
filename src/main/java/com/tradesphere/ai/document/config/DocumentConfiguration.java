package com.tradesphere.ai.document.config;

import com.tradesphere.ai.document.adapter.out.event.DocumentEventPublisher;
import com.tradesphere.ai.document.adapter.out.persistence.DocumentPersistenceAdapter;
import com.tradesphere.ai.document.adapter.out.storage.MinIOStorageAdapter;
import com.tradesphere.ai.document.adapter.out.virus.VirusScanStub;
import com.tradesphere.ai.document.application.port.in.*;
import com.tradesphere.ai.document.application.port.out.*;
import com.tradesphere.ai.document.application.service.DocumentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Document module configuration
 * Wires all hexagonal architecture components
 */
@Configuration
public class DocumentConfiguration {

    @Bean
    public DocumentService documentService(
            LoadDocumentPort loadDocumentPort,
            SaveDocumentPort saveDocumentPort,
            ListDocumentsPort listDocumentsPort,
            FileStoragePort fileStoragePort,
            VirusScanPort virusScanPort,
            PublishDocumentEventPort publishEventPort) {

        return new DocumentService(
            loadDocumentPort,
            saveDocumentPort,
            listDocumentsPort,
            fileStoragePort,
            virusScanPort,
            publishEventPort
        );
    }

    // Ports are automatically wired from adapters:
    // - DocumentPersistenceAdapter implements LoadDocumentPort, SaveDocumentPort, ListDocumentsPort
    // - MinIOStorageAdapter implements FileStoragePort
    // - VirusScanStub implements VirusScanPort
    // - DocumentEventPublisher implements PublishDocumentEventPort
}

