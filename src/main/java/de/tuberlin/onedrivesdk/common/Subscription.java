/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package de.tuberlin.onedrivesdk.common;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import de.tuberlin.onedrivesdk.OneDriveException;


/**
 * Subscription.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/07/03 umjammer initial version <br>
 * @see "https://tsmatz.wordpress.com/2016/04/22/onedrive-api-webhook-and-synchronization-delta/"
 * @see "https://docs.microsoft.com/ja-jp/graph/webhooks#notification-endpoint-validation"
 */
public class Subscription {

    /**
     * The SDK object.
     */
    protected ConcreteOneDriveSDK api;

    /**
     * The raw JSON which is received from the OneDrive API.
     */
    protected String rawJson = "";

    protected String expirationDateTime;
    protected String id;
    protected boolean muted;
    protected String notificationUrl;
    protected String resource;

    private static final Gson gson = new Gson();

    /**
     * Parse a OneItem object from JSON.
     *
     * @param json JSON from the OneDrive API
     * @return OneItem
     * @throws ParseException if the JSON can not be parsed
     * @throws OneDriveException if the JSON contains an OneDrive Error object from the API
     */
    public static Subscription fromJSON(String json) throws ParseException, OneDriveException {
        return gson.fromJson(json, Subscription.class);
    }

    /**
     * Sets the api object.
     *
     * @param api the api object
     * @return the identity
     * @throws OneDriveException if the api is null
     */
    public Subscription setApi(ConcreteOneDriveSDK api) throws OneDriveException {
        if (api == null) {
            throw new OneDriveException("The provided api object can not be null!");
        }
        this.api = api;
        return this;
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
    public Subscription setRawJson(String rawJson) {
        this.rawJson = rawJson;
        return this;
    }

    /** */
    public String getId() {
        return id;
    }

    /** */
    public Date getExpirationDateTime() throws java.text.ParseException {
        if (expirationDateTime != null && expirationDateTime.indexOf('.') != -1) {
            expirationDateTime = expirationDateTime.substring(0, expirationDateTime.indexOf('.'));
            DateFormat df = new SimpleDateFormat("y-M-d'T'H:m:s");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df.parse(expirationDateTime);
        } else {
            throw new java.text.ParseException(expirationDateTime, -1);
        }
    }

    /** */
    public void setExpirationDateTime(String expirationDateTime) {
        this.expirationDateTime = expirationDateTime;
    }

    /** */
    public void setId(String id) {
        this.id = id;
    }

    /** */
    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    /** */
    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    /** */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /** */
    public Subscription update() throws IOException, OneDriveException, ParseException, InterruptedException  {
        return api.updateSubscription(id);
    }

    /** */
    public void delete() throws IOException, OneDriveException, ParseException, InterruptedException  {
        api.deleteSubscription(id);
    }
}

/* */
