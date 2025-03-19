package de.tuberlin.onedrivesdk.uploadFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK.gson;
import static java.lang.System.getLogger;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.networking.OneResponse;
import de.tuberlin.onedrivesdk.networking.PreparedRequest;
import de.tuberlin.onedrivesdk.networking.PreparedRequestMethod;

/**
 * Implementation of OneUploadFile, blocking operation
 */
public class ConcreteOneUploadFile implements OneUploadFile {

    private static final Logger logger = getLogger(ConcreteOneUploadFile.class.getName());

    /** (use a multiple value of 320KB, best practice of dev.onedrive) */
    private static final int chunkSize = 320 * 1024 * 30;
    private final ReentrantLock shouldRun = new ReentrantLock(true);
    private final File fileToUpload;
    private OneDriveSDK api;
    private boolean canceled = false;
    private boolean finished = false;
    private UploadSession uploadSession;
    private final RandomAccessFile randFile;
    private String uploadUrl = "";

    public ConcreteOneUploadFile(OneFolder parentFolder,
                                 File fileToUpload, OneDriveSDK api) throws IOException {
        this(parentFolder, fileToUpload, fileToUpload.getName(), api);
    }

    public ConcreteOneUploadFile(OneFolder parentFolder,
             File fileToUpload, String filename, OneDriveSDK api) throws IOException {
        checkNotNull(parentFolder);
        this.api = checkNotNull(api);

        if (fileToUpload != null) {
            if (fileToUpload.isFile()) {
                if (fileToUpload.canRead()) {
                    this.fileToUpload = fileToUpload;
                    randFile = new RandomAccessFile(fileToUpload, "r");
                } else {
                    throw new IOException(String.format("File %s is not readable!", fileToUpload.getName()));
                }
            } else {
                throw new IOException(String.format("%s is not a File", fileToUpload.getAbsolutePath()));
            }
        } else {
            throw new NullPointerException("FileToUpload was null");
        }
        this.uploadSession = api.createUploadSession(parentFolder, filename);
        this.uploadUrl = this.uploadSession.getUploadURL();
    }

    @Override
    public long fileSize() {
        return fileToUpload.length();
    }

    @Override
    public long uploadStatus() throws IOException {
        if (uploadSession != null) {
            PreparedRequest request = new PreparedRequest(this.uploadUrl, PreparedRequestMethod.GET);
            OneResponse response = api.makeRequest(request);
            if (response.wasSuccess()) {
                return gson.fromJson(response.getBodyAsString(), UploadSession.class).getNextRange();
            } else {
                throw new OneDriveException(response.getBodyAsString());
            }
        }
        return 0;
    }

    @Override
    public OneFile startUpload() throws IOException {
        byte[] bytes;
        ConcreteOneFile finishedFile = null;

        OneResponse response;

        while (!canceled && !finished) {
            shouldRun.lock();

            long currFirstByte = randFile.getFilePointer();
            PreparedRequest uploadChunk = new PreparedRequest(this.uploadUrl, PreparedRequestMethod.PUT);

            if (currFirstByte + chunkSize < randFile.length()) {
                bytes = new byte[chunkSize];
            } else {
                // optimistic cast, assuming the last bit of the file is
                // never bigger than MAXINT
                bytes = new byte[(int) (randFile.length() - randFile.getFilePointer())];
            }
            long start = randFile.getFilePointer();
            randFile.readFully(bytes);

            uploadChunk.setBody(bytes);
            uploadChunk.addHeader("Content-Length", (randFile.getFilePointer() - start) + "");
            uploadChunk.addHeader(
                    "Content-Range",
                    "bytes %s-%s/%s".formatted(start, randFile.getFilePointer() - 1, randFile.length()));

            logger.log(Level.TRACE, "Uploading chunk {} - {}", start, randFile.getFilePointer() - 1);
            response = api.makeRequest(uploadChunk);
            if (response.wasSuccess()) {
                if (response.getStatusCode()==200 || response.getStatusCode()==201) { // if last chunk upload was successful end the
                    finished = true;
                    finishedFile = gson.fromJson(response.getBodyAsString(), ConcreteOneFile.class);

                } else {
                    //just continue
                    uploadSession = gson.fromJson(response.getBodyAsString(),
                                                  UploadSession.class);
                    randFile.seek(uploadSession.getNextRange());
                }
            } else {
                logger.log(Level.INFO, "Something went wrong while uploading last chunk. Trying to fetch upload status from server to retry");
                logger.log(Level.TRACE, response.getBodyAsString());
                response = api.makeRequest(this.uploadUrl, PreparedRequestMethod.GET, null);

                if (response.wasSuccess()) {
                    uploadSession = gson.fromJson(response.getBodyAsString(), UploadSession.class);
                    randFile.seek(uploadSession.getNextRange());
                    logger.log(Level.DEBUG, "Fetched updated uploadSession. Server requests {} as next chunk",uploadSession.getNextRange());

                } else {
                    canceled=true;
                    logger.log(Level.INFO, "Something went wrong while uploading. Was unable to fetch the currentUpload session from the Server");
                    randFile.close();
                    throw new OneDriveException(
                            String.format("Could not get current upload status from Server, aborting. Message was: %s", response.getBodyAsString()));
                }
            }
            shouldRun.unlock();
        }

        randFile.close();
        logger.log(Level.INFO, "finished upload");

        finishedFile.setApi(api);
        return finishedFile;

    }

    @Override
    public OneUploadFile pauseUpload() {
        logger.log(Level.INFO, "Pausing upload");
        shouldRun.lock();
        logger.log(Level.INFO, "Upload paused");
        return this;
    }

    @Override
    public OneUploadFile resumeUpload() {
        logger.log(Level.INFO, "Resuming upload");
        try {
            shouldRun.unlock();
            logger.log(Level.INFO, "Upload resumed");
        } catch (IllegalMonitorStateException e) {
            logger.log(Level.INFO, "Trying to resume an already running download");
        }
        return this;
    }

    @Override
    public OneUploadFile cancelUpload() throws IOException {
        logger.log(Level.INFO, "Canceling upload");
        this.canceled = true;
        if (uploadSession != null) {
            api.makeRequest(this.uploadUrl,
                    PreparedRequestMethod.DELETE, "");
            logger.log(Level.INFO, "Upload was canceled");
        }
        return this;
    }

    @Override
    public File getUploadFile() {
        return this.fileToUpload;
    }

    @Override
    public OneFile call() throws IOException {
        logger.log(Level.INFO, "Starting upload");
        return startUpload();
    }
}
