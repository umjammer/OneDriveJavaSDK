
package de.tuberlin.onedrivesdk.uploadFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import static com.google.common.base.Preconditions.checkNotNull;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.folder.ConcreteOneFolder;
import de.tuberlin.onedrivesdk.networking.OneDriveAuthenticationException;
import de.tuberlin.onedrivesdk.networking.OneResponse;
import de.tuberlin.onedrivesdk.networking.PreparedRequest;
import de.tuberlin.onedrivesdk.networking.PreparedRequestMethod;


/**
 * ConcreteOneUpload.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/06/18 umjammer initial version <br>
 */
public class ConcreteOneUpload implements OneUpload {

    private static final Logger logger = LogManager.getLogger(ConcreteOneUpload.class);

    private static final Gson gson = new Gson();

    private ConcreteOneDriveSDK api;

    private UploadSession uploadSession;

    private long size;

    private long offset;

    private Consumer<OneItem> finished;

    private String uploadUrl;

    public ConcreteOneUpload(ConcreteOneFolder parentFolder,
            String filename,
            long size,
            Consumer<OneItem> finished,
            ConcreteOneDriveSDK api) throws IOException, OneDriveAuthenticationException {
        checkNotNull(parentFolder);
        this.api = checkNotNull(api);
        this.size = size;
        this.finished = finished;
        this.uploadSession = api.createUploadSession(parentFolder, filename);
        this.uploadUrl = this.uploadSession.getUploadURL();
    }

    @Override
    public OutputStream upload() throws IOException, OneDriveException {

        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new UnsupportedOperationException();
            }
            @Override
            public void write(byte[] b, int ofs, int len) throws IOException {
                try {
                    byte[] content = Arrays.copyOfRange(b, ofs, ofs + len);

                    PreparedRequest uploadChunk = new PreparedRequest(uploadUrl, PreparedRequestMethod.PUT);

                    String range = String.format("bytes %s-%s/%s", offset, offset + content.length - 1, size);
                    uploadChunk.setBody(content);
                    uploadChunk.addHeader("Content-Length", String.valueOf(content.length));
                    uploadChunk.addHeader("Content-Range", range);

logger.trace("Uploading chunk: {}", range);
                    OneResponse response = api.makeRequest(uploadChunk);
                    if (response.wasSuccess()) {
                        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                            // if last chunk upload was successful end the finished =
                            // true;
                            ConcreteOneFile finishedFile = gson.fromJson(response.getBodyAsString(), ConcreteOneFile.class);
logger.info("finished upload");
                            finishedFile.setApi(api);
                            finished.accept(finishedFile);
                        } else {
                            // just continue
                            uploadSession = gson.fromJson(response.getBodyAsString(), UploadSession.class);
                            offset += content.length;
                        }
                    } else {
                        // TODO not tested
logger.info("Something went wrong while uploading last chunk. Trying to fetch upload status from server to retry");
logger.trace(response.getBodyAsString());
                        response = api.makeRequest(uploadUrl, PreparedRequestMethod.GET, null);

                        if (response.wasSuccess()) {
                            uploadSession = gson.fromJson(response.getBodyAsString(), UploadSession.class);
logger.debug("Fetched updated uploadSession. Server requests {} as next chunk", uploadSession.getNextRange());

                        } else {
logger.info("Something went wrong while uploading. Was unable to fetch the currentUpload session from the Server");
                            throw new OneDriveException(String
                                    .format("Could not get current upload status from Server, aborting. Message was: %s",
                                            response.getBodyAsString()));
                        }
                    }
                } catch (OneDriveException e) {
                    throw new IOException(e);
                }
            }
        };
    }
}
