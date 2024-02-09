package de.tuberlin.onedrivesdk.drive;

import com.google.gson.annotations.Expose;


/**
 * Data object for DriveOwner
 */
public class DriveOwner {
    @Expose
    public DriveUser user;

    public String toString() {
        return user.toString();
    }
}
