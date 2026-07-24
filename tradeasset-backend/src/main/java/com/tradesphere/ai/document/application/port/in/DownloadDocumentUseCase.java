package com.tradesphere.ai.document.application.port.in;

import java.io.InputStream;

/**
 * Use case for downloading documents
 */
public interface DownloadDocumentUseCase {

    InputStream downloadDocument(String documentId);
}

