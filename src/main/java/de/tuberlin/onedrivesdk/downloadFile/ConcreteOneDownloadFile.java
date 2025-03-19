package de.tuberlin.onedrivesdk.downloadFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import static java.lang.System.getLogger;


/**
 * Implementation of OneDownloadFile
 * Blocking download operation
 */
public class ConcreteOneDownloadFile implements OneDownloadFile {

    private static final Logger logger = getLogger(ConcreteOneDownloadFile.class.getName());

    private final OneFile metadata;
    private final OneDriveSDK api;
    private final File destinationFile;

    public ConcreteOneDownloadFile(ConcreteOneFile metadata, OneDriveSDK api, File destinationFile) throws FileNotFoundException {
        this.metadata = metadata;
        this.api = api;
        this.destinationFile = destinationFile;
    }

    @Override
    public OneFile getMetaData() {
        return metadata;
    }

    @Override
    public void startDownload() throws IOException {
        try (RandomAccessFile destination = new RandomAccessFile(this.destinationFile, "rw")) {
            logger.log(Level.INFO, "Starting download of " + metadata.getName());
            destination.write(api.download(metadata.getId()));
        } finally {
            logger.log(Level.INFO, "Finished download of " + this.metadata.getName());
        }
    }

    @Override
    public File getDownloadedFile() {
        return destinationFile;
    }
}
