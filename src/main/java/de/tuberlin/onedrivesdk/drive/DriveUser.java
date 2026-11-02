package de.tuberlin.onedrivesdk.drive;

import com.google.gson.annotations.Expose;


/**
 * Data object for drive user
 */
public class DriveUser {
    @Expose
    private String id;
    @Expose
    private String displayName;

    public DriveUser(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Owner: " + displayName + " - " + id;
    }
}
