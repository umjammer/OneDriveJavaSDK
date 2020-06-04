/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package de.tuberlin.onedrivesdk.downloadFile;

import java.io.IOException;
import java.io.InputStream;

import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.networking.OneDriveAuthenticationException;


/**
 * This Interface provides all Methods to handle a File download
 *
 */
public interface OneDownload {

    /**
     * Gets the meta data of the downloaded file.
     *
     * @return meta data
     */
    OneFile getMetaData();

    /**
     * Gets the file handle of the downloaded file.
     *
     * @return downloaded file
     */
    InputStream getDownloadedInputStream() throws IOException, OneDriveAuthenticationException;
}
