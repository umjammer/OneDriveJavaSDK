package de.tuberlin.onedrivesdk.drive;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.common.OneDriveError;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK.gson;
import static java.lang.System.getLogger;


/**
 * Implementation of the OneDrive Interface
 */
public class ConcreteOneDrive implements OneDrive {
    @Expose
    protected String id;
    protected ConcreteOneDriveSDK api;
    @Expose
    protected String driveType;
    @Expose
    protected DriveOwner owner;
    @Expose
    protected DriveQuota quota;
    protected String rawJson = "";

    private static final Logger logger = getLogger(ConcreteOneDrive.class.getName());

    private ConcreteOneDrive() {
    }

    public static List<OneDrive> parseDrivesFromJson(String json) throws OneDriveException {
        try {
            OneDriveError error;
            if ((error = OneDriveError.parseError(json)) != null) {
                throw new OneDriveException(error.toString());
            }

            JSONParser parser = new JSONParser();
            JSONObject root = null;

            try {
                root = (JSONObject) parser.parse(json);
            } catch (ParseException e) {
                logger.log(Level.WARNING, "Something failed while parsing Json {}", e.getMessage());
                logger.log(Level.DEBUG, "Exception while parsing", e);
            }

            JSONArray values = (JSONArray) root.get("value");
            json = values.toJSONString();

            List<OneDrive> oneDrives = gson.fromJson(json, new TypeToken<List<ConcreteOneDrive>>() {
            }.getType());

            return oneDrives;
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
    }

    public static ConcreteOneDrive fromJSON(String json) throws OneDriveException {
        try {
            OneDriveError error;
            if ((error = OneDriveError.parseError(json)) != null) {
                throw new OneDriveException(error.toString());
            }

            ConcreteOneDrive drive = gson.fromJson(json, ConcreteOneDrive.class);
            return drive.setRawJson(json);
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
    }

    public OneDrive setApi(ConcreteOneDriveSDK api) {
        this.api = api;
        return this;
    }

    public ConcreteOneDrive setRawJson(String rawJson) {
        this.rawJson = rawJson;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDriveType() {
        return driveType;
    }

    @Override
    public DriveUser getUser() {
        return owner.user;
    }

    @Override
    public DriveQuota getQuota() {
        return quota;
    }

    @Override
    public String toString() {
        return "Drive: "+ id + " - " + driveType + " " + owner;
    }

    @Override
    public OneFolder getRootFolder() throws IOException {
        return api.getRootFolder(this);
    }

    @Override
    public String getRawJson() {
        return rawJson;
    }
}
