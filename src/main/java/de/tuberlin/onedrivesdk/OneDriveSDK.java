package de.tuberlin.onedrivesdk;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.tuberlin.onedrivesdk.common.ConflictBehavior;
import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.common.OneItemType;
import de.tuberlin.onedrivesdk.common.Subscription;
import de.tuberlin.onedrivesdk.drive.OneDrive;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.networking.OneDriveAuthenticationException;
import de.tuberlin.onedrivesdk.networking.OneDriveSession;
import de.tuberlin.onedrivesdk.networking.OneResponse;
import de.tuberlin.onedrivesdk.networking.PreparedRequest;
import de.tuberlin.onedrivesdk.networking.PreparedRequestMethod;
import de.tuberlin.onedrivesdk.uploadFile.UploadSession;

/**
 * This interface provides the functionality of the OneDrive API.
 */
public interface OneDriveSDK {

    /**
     * Get user's default drive on OneDrive.
     *
     * @return OneDrive default drive
     * @throws IOException
     */
    OneDrive getDefaultDrive() throws IOException;

    /**
     * Gets drive by the specified drive id.
     *
     * @param driveId the drive id
     * @return OneDrive
     * @throws IOException
     */
    OneDrive getDrive(String driveId) throws IOException;

    /**
     * Gets all drives of the user.
     *
     * @return List<OneDrive>
     * @throws IOException
     * @throws OneDriveException
     */
    List<OneDrive> getAllDrives() throws IOException;

    /**
     * Gets the root folder of the default drive.
     *
     * @return root folder
     * @throws IOException
     */
    OneFolder getRootFolder() throws IOException;

    /**
     * Gets the root folder of the given dive.
     *
     * @param drive
     * @return root folder
     * @throws IOException
     */
    OneFolder getRootFolder(OneDrive drive) throws IOException;


    /**
     * Gets folder by id.
     *
     * @param id
     * @return OneFolder
     * @throws IOException
     */
    OneFolder getFolderById(String id) throws IOException;


    /**
     * Gets folder by path.
     *
     * @param pathToFolder
     * @return OneFolder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder getFolderByPath(String pathToFolder) throws IOException;


    /**
     * Gets file by id.
     *
     * @param id
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    OneFile getFileById(String id) throws IOException;


    /**
     * Gets file by path.
     *
     * @param pathToFile
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    OneFile getFileByPath(String pathToFile) throws IOException;


    /**
     * Gets folder by path.
     *
     * @param pathToFolder
     * @param drive
     * @return OneFolder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder getFolderByPath(String pathToFolder, OneDrive drive) throws IOException;

    /**
     * Gets file by path.
     *
     * @param pathToFile
     * @param drive
     * @return OneFile
     * @throws IOException
     * @throws OneDriveException
     */
    OneFile getFileByPath(String pathToFile, OneDrive drive) throws IOException;

    /**
     * Gets item by path.
     *
     * @param pathToFile
     * @return OneItem
     * @throws IOException
     * @throws OneDriveException
     */
    OneItem getItemByPath(String pathToFile) throws IOException;

    /**
     * @param url webhook url
     * @param clientState
     * @return Subscription
     */
    Subscription subscribe(String url, String clientState) throws IOException;

    /**
     * Used to authorize the session with the OAuth Response Code (used for first authentication)
     *
     * @param oAuthCode the code from the OneDrive OAuth authentication process.
     * @throws IOException
     * @throws OneDriveException
     */
    void authenticate(String oAuthCode) throws IOException;

    /**
     * Used to authorize the session with a RefreshToken
     *
     * @param refreshToken
     * @throws IOException
     * @throws OneDriveException
     */
    void authenticateWithRefreshToken(String refreshToken) throws IOException;

    /**
     * Returns the RefreshToken of the Current Session, if any exists and
     * the current session is valid.
     *
     * A refresh token is only generated if @see OneDriveScope.OFFLINE_ACCESS has been requested for this
     * session.
     *
     * <b><big>WARNING</big> The RefreshToken is equivalent to a user password and should be stored/encrypted similarly. </b>
     * @return
     * @throws OneDriveException
     */
    String getRefreshToken() throws OneDriveException;

    /**
     * Disconnect from the current session.
     *
     * @throws IOException
     */
    void disconnect() throws IOException;

    /**
     * Returns the OneDrive oAuth URL.
     * @return url
     */
    String getAuthenticationURL();

    /**
     * Returns true if the session is authenticated.
     * @return is authenticated
     */
    boolean isAuthenticated();

    /**
     * used to start a thread that request a new authentication token
     *
     * @see "OneDriveSession#refreshDelay"
     * @see OneDriveSession#startRefreshThread(Callback...)
     */
    void startSessionAutoRefresh(Callback... callbacks);

    @FunctionalInterface
    interface Callback {
        void exec();
    }

    /**
     * Deletes a OneDriveItem form OneDrive.
     *
     * @param oneItem to delete
     * @return true on success
     * @throws IOException
     * @throws OneDriveException
     */
    boolean deleteItem(OneItem oneItem) throws IOException;

    /**
     * Rename a file in OneDrive to a location in OneDrive.
     *
     * @param id OneDrive item id of the file to be copied
     * @param id2 id of the source folder
     * @param name the new name of the copied file
     * @return OneItem the renamed item
     * @throws IOException
     * @throws OneDriveException
     */
    OneItem rename(String id, String id2, String name) throws IOException;

    /**
     * Download a file from OneDrive by id and returns the byte[].
     *
     * @param id the OneDrive file id
     * @return byte[]
     * @throws OneDriveAuthenticationException
     */
    byte[] download(String id) throws IOException;

    /**
     * Download a file from OneDrive by id and returns the InputStream.
     *
     * @param id the OneDrive file id
     * @return InputStream
     * @throws OneDriveAuthenticationException
     */
    InputStream downloadAsStream(String id) throws IOException;

    /**
     * Copy and rename a file in OneDrive to a location in OneDrive.
     *
     * @param id            OneDrive item id of the file to be copied
     * @param id2 id of the target folder
     * @param name       the new name of the copied file
     * @return OneFile the copied file
     * @throws IOException
     */
    OneFile copy(String id, String id2, String name) throws IOException;

    /**
     * Move a file in OneDrive.
     *
     * @param id            OneDrive item id of the file to be moved
     * @param id2 id of the target folder
     * @return OneItem
     * @throws IOException
     * @throws OneDriveException
     */
    OneItem move(String id, String id2) throws IOException;

    /**
     * Create a new upload session in preparation of a file upload.
     *
     * @param parentFolder   on OneDrive
     * @param filename on OneDrive
     * @return UploadSession
     * @throws IOException
     */
    UploadSession createUploadSession(OneFolder parentFolder, String filename) throws IOException;

    /**
     * Perform the HTTP request to the OneDrive API.
     *
     * @param request
     * @return OneResponse
     * @throws IOException
     */
    OneResponse makeRequest(PreparedRequest request) throws IOException;

    /**
     * Perform the HTTP request to the OneDrive API with a json body.
     *
     * @param uploadUrl
     * @param get
     * @param json   body of the request
     * @return OneResponse
     * @throws IOException
     */
    OneResponse makeRequest(String uploadUrl, PreparedRequestMethod get, String json) throws IOException;

    /**
     * Gets all child folder of the specified folder.
     *
     * @param concreteOneFolder
     * @return children
     * @throws IOException
     * @throws OneDriveException
     */
    List<OneFolder> getChildFolder(OneFolder concreteOneFolder) throws IOException;

    /**
     * Gets all child files of the specified folder.
     *
     * @param concreteOneFolder
     * @return children
     * @throws OneDriveException
     */
    List<OneFile> getChildFiles(OneFolder concreteOneFolder) throws IOException;

    /**
     * Gets all children of the given folder depending on the type.
     *
     * @param concreteOneFolder
     * @param all
     * @return children
     * @throws OneDriveException
     */
    List<OneItem> getChildren(OneFolder concreteOneFolder, OneItemType all) throws IOException;

    /**
     * Create a new folder in OneDrive.
     *
     * @param concreteOneFolder the parent folder
     * @param name   name of the new folder
     * @return OneFolder the newly created folder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder createFolder(OneFolder concreteOneFolder, String name) throws IOException;

    /**
     * Create a new folder in OneDrive and define the behavior on folder name conflict.
     *
     * @param concreteOneFolder   the parent folder
     * @param name
     * @param behavior
     * @return OneFolder the newly created folder
     * @throws IOException
     * @throws OneDriveException
     */
    OneFolder createFolder(OneFolder concreteOneFolder, String name, ConflictBehavior behavior) throws IOException;
}
