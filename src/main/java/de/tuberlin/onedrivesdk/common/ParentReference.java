package de.tuberlin.onedrivesdk.common;

import com.google.gson.annotations.Expose;


/**
 * The parent folder reference of an item. Used for JSON transport.
 */
public class ParentReference {
    @Expose
    protected String driveId;
    @Expose
    protected String id;
    @Expose
    protected String path;

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
