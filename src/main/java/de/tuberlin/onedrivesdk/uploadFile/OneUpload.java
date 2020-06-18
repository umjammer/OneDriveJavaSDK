package de.tuberlin.onedrivesdk.uploadFile;

import java.io.IOException;
import java.io.OutputStream;

import de.tuberlin.onedrivesdk.OneDriveException;

/**
 * OneUpload.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/06/18 umjammer initial version <br>
 */
public interface OneUpload {

	/**
	 * Starts the upload, needs to be called to start the upload. Will throw exception when called while the download has already been started
	 * Blocks until either upload is finished or interrupted.
	 * @return the OneFile handle for the finished file
	 * @throws IOException 
	 */
	OutputStream upload() throws IOException, OneDriveException;
}
