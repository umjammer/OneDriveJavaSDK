package de.tuberlin.onedrivesdk.drive;

import com.google.gson.annotations.Expose;


/**
 * Data Object for drive qouta
 */
public class DriveQuota {
    @Expose
    private final long deleted = 0;
    @Expose
    private final long remaining = 0;
    @Expose
    private String state;
    @Expose
    private final long total = 0;
    @Expose
    private final long used = 0;

    public long getDeleted() {
        return deleted;
    }

    public long getRemaining() {
        return remaining;
    }

    public String getState() {
        return state;
    }

    public long getTotal() {
        return total;
    }

    public long getUsed() {
        return used;
    }
}
