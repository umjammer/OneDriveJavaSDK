package de.tuberlin.onedrivesdk;

import java.io.IOException;

/**
 * Exception that can be thrown on all API calls,
 * mostly JSON error responses from the oneDrive server
 */
public class OneDriveException extends IOException {

    public OneDriveException(String msg, Throwable reason) {
        super(msg, reason);
    }

    public OneDriveException(String msg) {
        super(msg);
    }

    public OneDriveException(Throwable cause) {
        super(cause);
    }
}
