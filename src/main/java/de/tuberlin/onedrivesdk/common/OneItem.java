package de.tuberlin.onedrivesdk.common;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.drive.DriveUser;
import de.tuberlin.onedrivesdk.file.ConcreteOneFile;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.ConcreteOneFolder;
import de.tuberlin.onedrivesdk.folder.OneFolder;

/**
 * The root class of all files and folder types that can be accessed through this sdk.
 */
public abstract class OneItem {

    private static final Logger logger = LogManager.getLogger(OneItem.class);

    /**
     * The SDK object.
     */
    protected OneDriveSDK api;

    /**
     * The OneDrive id of the resource.
     */
    protected String id = "";

    /**
     * The Name.
     */
    protected String name = "";

    /**
     * The created by reference. Possible keys are 'user', 'application' and 'device'.
     */
    protected Map<String, DriveUser> createdBy = new HashMap<>();

    /**
     * The creation timestamp of this item.
     */
    protected String createdDateTime;

    /**
     * The modified by reference. Possible keys are 'user', 'application' and 'device'.
     */
    protected Map<String, DriveUser> lastModifiedBy = new HashMap<>();

    /**
     * The last modified timestamp of this item.
     */
    protected String lastModifiedDateTime = "";

    /**
     * The cTag.
     */
    protected String cTag = "";

    /**
     * The eTag.
     */
    protected String eTag = "";

    /**
     * The size of an item in bytes.
     */
    protected long size = 0;

    /**
     * URL that displays the resource in the browser.
     */
    protected String webUrl = "";

    /**
     * The parent folder reference.
     */
    protected ParentReference parentReference;

    /**
     * The raw JSON which is received from the OneDrive API.
     */
    protected String rawJson = "";

    /**
     * A Url that can be used to download this file's content.
     */
    @SerializedName("@content.downloadUrl")
    protected String downloadUrl;

    /**
     * A timestamp of the last refresh.
     */
    private long lastRefresh;

    /** */
    private static Gson gson = new Gson();

    /**
     * Parse a OneItem object from JSON.
     *
     * @param json JSON from the OneDrive API
     * @return OneItem
     * @throws ParseException if the JSON can not be parsed
     * @throws OneDriveException if the JSON contains an OneDrive Error object from the API
     */
    public static OneItem fromJSON(String json) throws OneDriveException {
        try {
            JSONObject root = getJsonObject(json);

            OneDriveError error;
            if ((error = OneDriveError.parseError(json)) != null) {
                throw new OneDriveException(error.toString());
            }

            if (root.containsKey("file")) {
                return gson.fromJson(json, ConcreteOneFile.class).setLastRefresh(System.currentTimeMillis());
            } else {
                return gson.fromJson(json, ConcreteOneFolder.class).setLastRefresh(System.currentTimeMillis());
            }
        } catch (ParseException e) {
            throw new OneDriveException("API - response could not be processed", e);
        }
    }

    /**
     * Parse a List of OneItems from JSON.
     *
     * @param json JSON from the OneDrive API
     * @return a List of OneItems
     * @throws ParseException if the JSON can not be parsed
     * @throws OneDriveException if the JSON contains an OneDrive Error object from the API
     */
    public static List<OneItem> parseItemsFromJson(String json) throws IOException {
        return OneItem.parseItemsFromJson(json, OneItemType.ALL);
    }

    /**
     * Parse a List of OneItems from JSON.
     *
     * @param json JSON from the OneDrive API
     * @param type OneItemType, can be used to define which type of items should be parsed
     * @return items from json
     * @throws ParseException if the JSON can not be parsed
     * @throws OneDriveException if the json dose not contain a 'value' attribute
     */
    public static List<OneItem> parseItemsFromJson(String json, OneItemType type) throws IOException {
        ArrayList<OneItem> itemList = new ArrayList<>();

        JSONObject root = getJsonObject(json);

//System.err.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(json)));

        if (root.containsKey("value")) {
            JSONArray values = (JSONArray) root.get("value");
            for (Object object : values) {
                JSONObject itemJson = (JSONObject) object;
                OneItem item = OneItem.fromJSON(itemJson.toJSONString());
                switch (type) {
                    case FILE:
                        if (item instanceof OneFile) itemList.add(item);
                        break;
                    case FOLDER:
                        if (item instanceof OneFolder) itemList.add(item);
                        break;
                    case ALL:
                        itemList.add(item);
                        break;
                }
            }
        } else {
            throw new OneDriveException("Cannot parse items from JSON. Missing argument 'value'.");
        }

        return itemList;
    }

    /**
     * Parse JSON from string and return the JSONObject
     *
     * @param json from the OneDrive API
     * @return JSONObject
     * @throws ParseException if the JSON can not be parsed
     */
    private static JSONObject getJsonObject(String json) throws OneDriveException {
        try {
            JSONParser parser = new JSONParser();
            JSONObject root;
            root = (JSONObject) parser.parse(json);
            return root;
        } catch (ParseException e) {
            throw new OneDriveException(e);
        }
    }

    /**
     * Gets the name of the item.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the api object.
     *
     * @param api the api object
     * @return the identity
     * @throws OneDriveException if the api is null
     */
    public OneItem setApi(OneDriveSDK api) throws OneDriveException {
        if (api == null) {
            throw new OneDriveException("The provided api object can not be null!");
        }
        this.api = api;
        return this;
    }

    /**
     * Gets the id of the item.
     *
     * @return id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Delete the item.
     *
     * @return true if the item was deleted from OneDrive.
     * @throws IOException
     * @throws OneDriveException
     */
    public boolean delete() throws IOException {
        return this.api.deleteItem(this);
    }

    /**
     * Gets the cTag.
     *
     * @return cTag
     */
    public String getCTag() {
        return this.cTag;
    }

    /**
     * Gets the eTag.
     *
     * @return eTag
     */
    public String getETag() {
        return this.eTag;
    }

    /**
     * The created by reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return created by
     */
    public Map<String, DriveUser> getCreatedBy() {
        return this.createdBy;
    }

    /**
     * The creation timestamp of this item.
     *
     * @return unix formatted timestamp
     */
    public long getCreatedDateTime() {
        try {
            if (createdDateTime != null) {
                return LocalDateTime.parse(createdDateTime.replaceFirst("Z$", "")).toEpochSecond(ZoneOffset.UTC);
            }
        } catch (DateTimeParseException e) {
logger.warn(e.getMessage() + " " + createdDateTime);
        }
        return 0;
    }

    /**
     * The modified by reference. Possible keys are 'user', 'application' and 'device'.
     *
     * @return last modified by
     */
    public Map<String, DriveUser> getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    /**
     * The last modified timestamp of this item.
     *
     * @return unix formatted timestamp
     */
    public long getLastModifiedDateTime() {
        try {
            if (lastModifiedDateTime != null) {
                return LocalDateTime.parse(lastModifiedDateTime.replaceFirst("Z$", "")).toEpochSecond(ZoneOffset.UTC);
            }
        } catch (DateTimeParseException e) {
logger.warn(e.getMessage() + " " + lastModifiedDateTime);
        }
        return 0;
    }

    /**
     * Gets the size of this item in bytes.
     *
     * @return size
     */
    public long getSize() {
        return this.size;
    }

    /**
     * Gets the URL that displays the resource in the browser.
     *
     * @return web url
     */
    public String getWebUrl() {
        return this.webUrl;
    }

    /**
     * Gets the parent folder.
     *
     * @return parent folder
     * @throws IOException
     * @throws OneDriveException
     */
    public OneFolder getParentFolder() throws IOException {
        return api.getFolderById(this.parentReference.id);
    }

    /**
     * Gets the raw JSON which is received from the OneDrive API.
     *
     * @return raw json
     */
    public String getRawJson() {
        return rawJson;
    }

    /**
     * Sets the raw json.
     *
     * @param rawJson json
     * @return raw json
     */
    public OneItem setRawJson(String rawJson) {
        this.rawJson = rawJson;
        return this;
    }

    /**
     * Gets the timestamp of the last refresh.
     *
     * @return timestamp in milliseconds
     */
    public long getLastRefresh() {
        return lastRefresh;
    }

    /**
     * Sets the timestamp of the last refresh.
     *
     * @param lastRefresh timestamp in milliseconds
     * @return last refresh
     */
    public OneItem setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
        return this;
    }

    /**
     * Refresh the item state.
     *
     * @return item the reference of this item will be another one.
     * @throws IOException
     * @throws OneDriveException
     */
    public OneItem refreshItem() throws IOException {
        if(this instanceof OneFile){
            return (OneItem) api.getFileById(id);
        } else {
            return (OneItem) api.getFolderById(id);
        }
    }

    /**
     * Is file.
     *
     * @return boolean
     */
    public abstract boolean isFile();

    /**
     * Is folder.
     *
     * @return boolean
     */
    public abstract boolean isFolder();


    /**
     * Rename this file in the target folder.
     *
     * @param sourceFolder destination folder
     * @param name a new name for the file
     * @return the new created reference of the file in the new folder
     * @throws IOException
     * @throws OneDriveException
     * @throws ParseException
     * @throws InterruptedException
     */
    public OneItem rename(OneFolder sourceFolder, String name) throws IOException {
        return api.rename(id, sourceFolder.getId(), name);
    }
}
