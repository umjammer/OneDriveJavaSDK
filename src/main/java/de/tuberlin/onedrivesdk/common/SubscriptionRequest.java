/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package de.tuberlin.onedrivesdk.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * SubscriptionRequest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/07/03 umjammer initial version <br>
 * @see "https://tsmatz.wordpress.com/2016/04/22/onedrive-api-webhook-and-synchronization-delta/"
 * @see "https://github.com/OneDrive/onedrive-webhooks-aspnet/blob/master/OneDriveWebhooks/Models/OneDriveSubscription.cs"
 */
class SubscriptionRequest {

    protected List<String> scenarios = Arrays.asList("Webhook");
    // "https://docs.microsoft.com/ja-jp/graph/api/subscription-post-subscriptions?view=graph-rest-1.0&tabs=http"
    // doesn't work
//    protected String changeType = "updated";
    // TODO might work
//    protected String resource = "/me/drive/root";
    // TODO might not work
//    protected String id;
    protected String notificationUrl;
    protected String clientState;

    /** */
    public SubscriptionRequest(String notificationUrl, String clientState) {
        this.notificationUrl = notificationUrl;
        this.clientState = clientState;
    }

    protected String expirationDateTime = getExpireTime();

    static String getExpireTime() {
        long time = System.currentTimeMillis() + 24 * 60 * 60 * 1000; // TODO what is default length? this is for graph
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date(time));
    }
}

class SubscriptionUpdateRequest {
    protected String expirationDateTime = SubscriptionRequest.getExpireTime();
}

/* */
