package de.tuberlin.onedrivesdk.common;

import java.util.Arrays;

/**
 * The different scopes for using the OneDrive API. These scopes are used in the authentication process.
 */
public enum OneDriveScope {
    SIGNIN("wl.signin"),
    OFFLINE_ACCESS("wl.offline_access"),
    READONLY("onedrive.readonly"),
    READWRITE("onedrive.readwrite"),
    APPFOLDER("onedrive.appfolder");

    private String code;

    OneDriveScope(String s){
        this.code = s;
    }

    public String getCode(){
        return code;
    }

    public static OneDriveScope valueOfByMicrosoftScopeString(String scope) {
    	return Arrays.stream(values()).filter(e -> e.code.equals(scope)).findFirst().get();
    }
}
