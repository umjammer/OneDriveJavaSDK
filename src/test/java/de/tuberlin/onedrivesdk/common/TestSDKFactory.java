package de.tuberlin.onedrivesdk.common;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

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
        } catch (OneDriveException e) {
            Assertions.fail(e.getMessage());
            return null;
        }
    }

}
