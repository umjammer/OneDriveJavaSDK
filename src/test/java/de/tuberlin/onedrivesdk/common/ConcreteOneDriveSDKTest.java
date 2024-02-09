package de.tuberlin.onedrivesdk.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.tuberlin.onedrivesdk.OneDriveFactory;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.downloadFile.OneDownloadFile;
import de.tuberlin.onedrivesdk.drive.OneDrive;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.uploadFile.OneUploadFile;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class ConcreteOneDriveSDKTest {

    static boolean localPropertiesExists() {
            return java.nio.file.Files.exists(Paths.get("credentials.properties"));
        }

    @Test
    @Disabled
    public void uploadBigFile() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();

        int fileLength = 10000;
        String fileName = "src/test/resources/uploadTest.big";
        String targetPath = "/IntegrationTesting/FolderForUploads";
        String downloadDestination = "src/test/resources/uploadTest_download.big";

        File localFile = new File(fileName);
        File destinationFile = new File(downloadDestination);

        ConcreteOneDriveSDKTest.generateFile(fileName, fileLength);

        HashCode sourceHash = Files.asByteSource(localFile).hash(Hashing.sha256());

        OneFolder targetFolder = api.getFolderByPath(targetPath);
        OneUploadFile upload = targetFolder.uploadFile(localFile);

        upload.startUpload();

        Thread.sleep(2000);

        OneFile remoteFile = api.getFileByPath("/IntegrationTesting/FolderForUploads/" + localFile.getName());
        assertEquals(sourceHash.toString().toUpperCase(), remoteFile.getSHA1Hash());

        OneDownloadFile downloadedFile = remoteFile.download(destinationFile);
        downloadedFile.startDownload();

        HashCode downloadedHash = Files.asByteSource(destinationFile).hash(Hashing.sha256());

        if (!localFile.delete())
            System.err.println("Local file could not be deleted.");

        if (!destinationFile.delete())
            System.err.println("Downloaded file could not be deleted.");

        assertEquals(sourceHash.toString().toUpperCase(), downloadedHash.toString().toUpperCase());
    }

    private static void generateFile(String fileName, long fileLength) throws Exception {
        File f = new File(fileName);

        OutputStream out = new FileOutputStream(f);

        byte[] randomBytes = new byte[(int) (1024 * fileLength)];
        new Random().nextBytes(randomBytes);
        out.write(randomBytes);

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @EnabledIf("localPropertiesExists")
    public void testGetAllDrives() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        List<OneDrive> drives = api.getAllDrives();
        assertEquals(1, drives.size());
        assertEquals(drives.get(0).getDriveType(), "personal");
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testGetDefaultDrive() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneDrive drive = api.getDefaultDrive();
        assertNotNull(drive);
        assertEquals("3fb7bc4f1939ee71", drive.getId());
    }

    @Test
    @EnabledIf("localPropertiesExists")
    public void testRootFolder() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneDrive drive = api.getDefaultDrive();
        OneFolder rootFolder = api.getRootFolder(drive);
        OneFolder folder = api.getRootFolder(drive);
        assertEquals("root", folder.getName());
        assertEquals(rootFolder.getName(), folder.getName());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testGetFileByPath() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        assertEquals("Image.jpg", api.getFileByPath("/IntegrationTesting/Image.jpg").getName());
    }

    @Test
    @EnabledIf("localPropertiesExists")
    public void testFileNotFound() {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        assertThrows(IOException.class, () -> {
            api.getFileByPath("/File/Not/Found.txt");
        });
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testGetFileById() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        assertEquals("Image.jpg", api.getFileById("3FB7BC4F1939EE71!105").getName());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testFolderByPath() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        assertEquals("SecondLevelFolder", api.getFolderByPath("/IntegrationTesting/SecondLevelFolder").getName());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testGetFolderById() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        assertEquals("root", api.getFolderById("3FB7BC4F1939EE71!103").getName());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testGetDriveById() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        assertEquals("3fb7bc4f1939ee71", api.getDrive("3fb7bc4f1939ee71").getId());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testChildCount() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFolder folder = api.getFolderByPath("/IntegrationTesting");

        assertEquals(6, folder.getChildCount());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testChildFolder() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFolder folder = api.getFolderByPath("/IntegrationTesting");

        List<String> expectedChildren = Arrays.asList("FolderForUploads", "FolderForDownload", "SecondLevelFolder", "FolderForFolderCreation", "FolderForMoveAndCopy");
        List<OneFolder> children = folder.getChildFolder();

        assertEquals(expectedChildren.size(), children.size());

        for (OneFolder child : children) {
            assertTrue(expectedChildren.contains(child.getName()));
        }

        for (String expectedChild : expectedChildren) {
            boolean found = false;
            for (OneFolder child : children) {
                if (child.getName().equals(expectedChild)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testChildFiles() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFolder folder = api.getFolderByPath("/IntegrationTesting");

        List<String> expectedChildren = List.of("Image.jpg");
        List<OneFile> children = folder.getChildFiles();

        assertEquals(1, children.size());

        for (OneFile child : children) {
            assertTrue(expectedChildren.contains(child.getName()));
        }

        for (String expectedChild : expectedChildren) {
            boolean found = false;
            for (OneFile child : children) {
                if (child.getName().equals(expectedChild)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testGetChildren() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFolder folder = api.getFolderByPath("/IntegrationTesting");

        int expectedFiles = 1;
        int expectedFolder = 5;
        int fileCount = 0;
        int folderCount = 0;

        List<OneItem> children = folder.getChildren();
        for (OneItem child : children) {
            if (child.isFile())
                fileCount++;
            if (child.isFolder())
                folderCount++;
        }

        assertEquals(expectedFolder, folderCount);
        assertEquals(expectedFiles, fileCount);
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testCreateAndDeleteFolder() throws Exception {
        String folderName = "TestFolder";
        String path = "/IntegrationTesting/FolderForFolderCreation";

        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFolder targetFolder = api.getFolderByPath(path);

        int folderCount = targetFolder.getChildCount();

        OneFolder createdFolder = targetFolder.createFolder(folderName);
        OneFolder secondFolder = targetFolder.createFolder(folderName,ConflictBehavior.RENAME);

        targetFolder = targetFolder.refresh();
        boolean rightFolderCount = (folderCount + 2) == targetFolder.getChildCount();

        assertTrue(rightFolderCount);
        assertEquals(folderName, createdFolder.getName());
        assertEquals(folderName+" 1", secondFolder.getName());

        //delete folder (cleanup)
        if (rightFolderCount) {
            createdFolder.delete();
            secondFolder.delete();
            targetFolder = targetFolder.refresh();
            assertEquals(folderCount, targetFolder.getChildCount());
        }
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testRefresh() throws Exception {
        String folderName = "TestFolder";
        String path = "/IntegrationTesting/FolderForFolderCreation";

        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFolder targetFolder = api.getFolderByPath(path);

        OneFolder createdFolder = targetFolder.createFolder(folderName);

        OneFolder refreshFolder = targetFolder.refresh();

        assertTrue(refreshFolder.getLastRefresh() > targetFolder.getLastRefresh());

        assertEquals(targetFolder.getId(), refreshFolder.getId());
        assertEquals(targetFolder.getName(), refreshFolder.getName());
        assertNotEquals(targetFolder.getChildCount(), refreshFolder.getChildCount());

        OneFolder refetchFolder = api.getFolderByPath(path);

        assertEquals(refetchFolder.getId(), refreshFolder.getId());
        assertEquals(refetchFolder.getName(), refreshFolder.getName());
        assertEquals(refetchFolder.getChildCount(), refreshFolder.getChildCount());

        assertTrue(refreshFolder.getLastRefresh() < refetchFolder.getLastRefresh());
        assertTrue(refetchFolder != targetFolder && refreshFolder != targetFolder);

        createdFolder.delete();
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testDeleteFile() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        String testFileName = "uploadTest.jpg";
        String targetPath = "/IntegrationTesting/FolderForUploads";

        File localFile = new File("src/test/resources/"+testFileName);

        OneFolder targetFolder = api.getFolderByPath(targetPath);
        OneUploadFile upload = targetFolder.uploadFile(localFile);
        upload.call();
        upload.pauseUpload();
        upload.uploadStatus();
        upload.resumeUpload();

        targetFolder = targetFolder.refresh();

        int childCount = targetFolder.getChildCount();
        List<OneFile> files = targetFolder.getChildFiles();
        OneFile fileToDelete = null;

        for (OneFile file : files) {
            if (file.getName().equals(testFileName)) {
                fileToDelete = file;
            }
        }

        if (fileToDelete != null) {
            fileToDelete.delete();
            targetFolder = targetFolder.refresh();
            assertEquals(childCount - 1, targetFolder.getChildCount());
        } else {
            fail("file with name '" + testFileName + "' not found");
        }
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testCopyFile() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFile file = api.getFileByPath("/IntegrationTesting/FolderForMoveAndCopy/Image.jpg");
        OneFolder targetFolder = api.getFolderByPath("/IntegrationTesting/FolderForMoveAndCopy/CopyTarget");

        int itemCount = targetFolder.getChildCount();

        OneFile newFile = file.copy(targetFolder);
        targetFolder = targetFolder.refresh();

        if (newFile != null) {
            newFile.delete();
        }

        assertEquals(itemCount + 1, targetFolder.getChildCount());
        assertEquals(newFile.getName(), file.getName());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testMoveFile() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFile file = api.getFileByPath("/IntegrationTesting/FolderForMoveAndCopy/ImageForMove.jpg");
        OneFolder sourceFolder = file.getParentFolder();
        OneFolder targetFolder = api.getFolderByPath("/IntegrationTesting/FolderForMoveAndCopy/MoveTarget");

        int sourceItemCount = sourceFolder.getChildCount();
        int targetItemCount = targetFolder.getChildCount();

        assertEquals(sourceFolder.getId(), file.getParentFolder().getId());

        OneFile newFile = (OneFile) file.move(targetFolder);
        file = file.refresh();

        assertEquals(targetFolder.getId(), file.getParentFolder().getId());

        targetFolder = targetFolder.refresh();
        sourceFolder = sourceFolder.refresh();

        newFile.move(sourceFolder);

        assertEquals(sourceItemCount - 1, sourceFolder.getChildCount());
        assertEquals(targetItemCount + 1, targetFolder.getChildCount());
        assertEquals(newFile.getName(), file.getName());
    }

    @Test
    @Disabled("do integration test on project vavi-nio-file-onedrive")
    public void testParentFolder() throws Exception {
        OneDriveSDK api = ConcreteOneDriveSDKTest.connect();
        OneFolder folder = api.getFolderByPath("IntegrationTesting");
        assertEquals("root", folder.getParentFolder().getName());
    }

    @Test
    @EnabledIf("localPropertiesExists")
    public void testFactory() {
        assertNotNull(new OneDriveFactory());
        assertNotNull(OneDriveFactory.createOneDriveSDK(OneDriveCredentials.getClientId(), OneDriveCredentials.getClientSecret(), OneDriveScope.READWRITE));
        assertNotNull(OneDriveFactory.createOneDriveSDK(OneDriveCredentials.getClientId(), OneDriveCredentials.getClientSecret(),"",OneDriveScope.READWRITE));
        assertNotNull(OneDriveFactory.createOneDriveSDK(OneDriveCredentials.getClientId(), OneDriveCredentials.getClientSecret(), "", new ExceptionEventHandler() {
            @Override
            public void handle(Exception e) {

            }

            @Override
            public void handle(Object src, Exception e) {

            }
        }, OneDriveScope.READWRITE));
    }

    private static OneDriveSDK connect(){
        return TestSDKFactory.getInstance();
    }
}
