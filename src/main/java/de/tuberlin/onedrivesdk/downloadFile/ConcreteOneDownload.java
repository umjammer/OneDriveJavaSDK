/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package de.tuberlin.onedrivesdk.downloadFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;


/**
 * Implementation of OneDownload Blocking download operation
 */
public class ConcreteOneDownload implements OneDownload {

    private final OneFile metadata;
    private final OneDriveSDK api;

    public ConcreteOneDownload(ConcreteOneFile metadata, OneDriveSDK api) throws FileNotFoundException {
        this.metadata = metadata;
        this.api = api;
    }

    @Override
    public OneFile getMetaData() {
        return metadata;
    }

    @Override
    public InputStream getDownloadedInputStream() throws IOException {
        return api.downloadAsStream(metadata.getId());
    }
}
