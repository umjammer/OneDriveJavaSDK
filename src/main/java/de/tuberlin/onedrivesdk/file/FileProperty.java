package de.tuberlin.onedrivesdk.file;

import java.util.HashMap;

import com.google.gson.annotations.Expose;


/**
 * Data object for file metadata in one drive
 */
public class FileProperty {
    @Expose
    protected HashMap<String, String> hashes;
    @Expose
    protected String mimeType;
}
