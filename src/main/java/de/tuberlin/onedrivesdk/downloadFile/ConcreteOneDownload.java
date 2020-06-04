/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package de.tuberlin.onedrivesdk.downloadFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.networking.OneDriveAuthenticationException;


/**
 * Implementation of OneDownload Blocking download operation
 */
public class ConcreteOneDownload implements OneDownload {

    private final ConcreteOneFile metadata;
    private final ConcreteOneDriveSDK api;

    public ConcreteOneDownload(ConcreteOneFile metadata, ConcreteOneDriveSDK api) throws FileNotFoundException {
        this.metadata = metadata;
        this.api = api;
    }

    @Override
    public OneFile getMetaData() {
        return metadata;
    }

    @Override
    public InputStream getDownloadedInputStream() throws IOException, OneDriveAuthenticationException {
        return api.downloadAsStream(metadata.getId());
    }
}
