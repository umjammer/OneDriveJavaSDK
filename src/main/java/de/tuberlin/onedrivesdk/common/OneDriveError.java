package de.tuberlin.onedrivesdk.common;

import com.google.gson.annotations.Expose;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK.gson;


/**
 * Data object for json transport
 */
public class OneDriveError {
    @Expose
    InnerError error;

    public static OneDriveError parseError(String json)  throws ParseException{
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(json);
        if (root.containsKey("error")) {
            return gson.fromJson(json, OneDriveError.class);
        }

        return null;
    }

    @Override
    public String toString() {
        return "Error: " + error;
    }
}
