package de.tuberlin.onedrivesdk.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.downloadFile.ConcreteOneDownload;
import de.tuberlin.onedrivesdk.downloadFile.ConcreteOneDownloadFile;
import de.tuberlin.onedrivesdk.downloadFile.OneDownload;
import de.tuberlin.onedrivesdk.downloadFile.OneDownloadFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;

/**
 * Implementation of OneFile using methods from ConcreteOneDriveSDK
 */
public class ConcreteOneFile extends OneItem implements OneFile {

    private FileProperty file;

    private ConcreteOneFile() {
    }

    @Override
    public String toString() {
        return "(F) " + name;
    }

    public OneDownloadFile download(File targetFile) throws FileNotFoundException {
        return new ConcreteOneDownloadFile(this,api,targetFile);
    }

    @Override
    public OneDownload download() throws IOException {
        return new ConcreteOneDownload(this, api);
    }

    @Override
    public String getDownloadUrl() {
        return this.downloadUrl;
    }

    @Override
    public OneFile refresh() throws IOException {
        return (OneFile) super.refreshItem();
    }

    @Override
    public String getCRC32Hash() {
        return this.file.hashes.get("crc32Hash");
    }

    @Override
    public String getSHA1Hash() {
        return this.file.hashes.get("sha1Hash");
    }

    @Override
    public String getMimeType() {
        return this.file.mimeType;
    }

    @Override
    public OneFolder getParentFolder() throws IOException {
        return super.getParentFolder();
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public OneFile copy(OneFolder targetFolder) throws IOException {
        return this.copy(targetFolder, null);
    }

    @Override
    public OneFile copy(OneFolder targetFolder, String name) throws IOException {
        return api.copy(id, targetFolder.getId(), name);
    }

    @Override
    public OneItem move(OneFolder targetFolder) throws IOException {
        return api.move(id, targetFolder.getId());
    }
}
