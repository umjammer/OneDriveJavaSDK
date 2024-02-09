package de.tuberlin.onedrivesdk.common;

import com.google.gson.annotations.Expose;


/**
 * Data object for json transport
 */
public class InnerError {
    @Expose
    String code;
    @Expose
    String message;
    @Expose
    String target;
    @Expose
    InnerError details;
    @Expose
    InnerError innererror;

    @Override
    public String toString() {
        return "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ((target != null) ? ", target='" + target + '\'' : "") +
                ((details != null) ? ", details: {" + details + "}" : "") +
                ((innererror != null) ? ", innererror: {" + innererror + "}" : "");
    }
}
