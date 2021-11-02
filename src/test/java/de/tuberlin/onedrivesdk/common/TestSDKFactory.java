package de.tuberlin.onedrivesdk.common;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;

import de.tuberlin.onedrivesdk.OneDriveSDK;

/**
 * Created by Sebastian on 10.06.2015.
 */
public class TestSDKFactory {

    public static OneDriveSDK getInstance(){

        try {
            return ConcreteOneDriveSDK.createFromSession(SessionProvider.getSession());
        } catch (IOException e) {
            Assertions.fail(e.getMessage());
            return null;
        }
    }

}
