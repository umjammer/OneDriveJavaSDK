package de.tuberlin.onedrivesdk.common;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveFactory;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.downloadFile.OneDownloadFile;
import de.tuberlin.onedrivesdk.drive.OneDrive;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import de.tuberlin.onedrivesdk.uploadFile.OneUploadFile;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ConcreteOneDriveSDKTest {

    @Test
    public void uploadBigFile() throws IOException, OneDriveException, NoSuchAlgorithmException, InterruptedException {
        OneDriveSDK api = this.connect();

        int fileLength = 10000;
        String fileName = "src/test/resources/uploadTest.big";
        String targetPath = "/IntegrationTesting/FolderForUploads";
        String downloadDestination = "src/test/resources/uploadTest_download.big";

        File localFile = new File(fileName);
        File destinationFile = new File(downloadDestination);

        this.generateFile(fileName, fileLength);

        HashCode sourceHash = Files.hash(localFile, Hashing.sha1());

        OneFolder targetFolder = api.getFolderByPath(targetPath);
        final OneUploadFile upload = targetFolder.uploadFile(localFile);

        upload.startUpload();

        Thread.sleep(2000);

        OneFile remoteFile = api.getFileByPath("/IntegrationTesting/FolderForUploads/" + localFile.getName());
        Assertions.assertEquals(sourceHash.toString().toUpperCase(), remoteFile.getSHA1Hash());

        OneDownloadFile downloadedFile = remoteFile.download(destinationFile);
        downloadedFile.startDownload();

        HashCode downloadedHash = Files.hash(destinationFile, Hashing.sha1());

        if (!localFile.delete())
            System.err.println("Local file could not be deleted.");

        if (!destinationFile.delete())
            System.err.println("Downloaded file could not be deleted.");

        Assertions.assertEquals(sourceHash.toString().toUpperCase(), downloadedHash.toString().toUpperCase());
    }

    private void generateFile(String fileName, long fileLength) throws IOException {
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
    public void testGetAllDrives() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        List<OneDrive> drives = api.getAllDrives();
        Assertions.assertTrue(drives.size() == 1);
        Assertions.assertEquals(drives.get(0).getDriveType(), "personal");
    }

    @Test
    public void testGetDefaultDrive() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        OneDrive drive = api.getDefaultDrive();
        Assertions.assertNotNull(drive);
        Assertions.assertEquals("3fb7bc4f1939ee71", drive.getId());
    }

    @Test
    public void testRootFolder() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        OneDrive drive = api.getDefaultDrive();
        OneFolder rootFolder = api.getRootFolder(drive);
        OneFolder folder = api.getRootFolder(drive);
        Assertions.assertEquals(folder.getName(), "root");
        Assertions.assertEquals(folder.getName(), rootFolder.getName());
    }

    @Test
    public void testGetFileByPath() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        Assertions.assertEquals("Image.jpg", api.getFileByPath("/IntegrationTesting/Image.jpg").getName());
    }

    @Test
    public void testFileNotFound() {
        OneDriveSDK api = this.connect();
        try {
            api.getFileByPath("/File/Not/Found.txt");
            Assertions.fail();
        } catch (IOException e) {
        } catch (OneDriveException e) {
        }
    }

    @Test
    public void testGetFileById() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        Assertions.assertEquals("Image.jpg", api.getFileById("3FB7BC4F1939EE71!105").getName());
    }

    @Test
    public void testFolderByPath() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        Assertions.assertEquals("SecondLevelFolder", api.getFolderByPath("/IntegrationTesting/SecondLevelFolder").getName());
    }

    @Test
    public void testGetFolderById() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        Assertions.assertEquals("root", api.getFolderById("3FB7BC4F1939EE71!103").getName());
    }

    @Test
    public void testGetDriveById() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        Assertions.assertEquals("3fb7bc4f1939ee71", api.getDrive("3fb7bc4f1939ee71").getId());
    }

    @Test
    public void testChildCount() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        OneFolder folder = api.getFolderByPath("/IntegrationTesting");

        Assertions.assertEquals(6, folder.getChildCount());
    }

    @Test
    public void testChildFolder() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        OneFolder folder = api.getFolderByPath("/IntegrationTesting");

        List<String> expectedChildren = Arrays.asList("FolderForUploads", "FolderForDownload", "SecondLevelFolder", "FolderForFolderCreation", "FolderForMoveAndCopy");
        List<OneFolder> children = folder.getChildFolder();

        Assertions.assertEquals(expectedChildren.size(), children.size());

        for (OneFolder child : children) {
            Assertions.assertTrue(expectedChildren.contains(child.getName()));
        }

        for (String expectedChild : expectedChildren) {
            boolean found = false;
            for (OneFolder child : children) {
                if (child.getName().equals(expectedChild)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found);
        }
    }

    @Test
    public void testChildFiles() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        OneFolder folder = api.getFolderByPath("/IntegrationTesting");

        List<String> expectedChildren = Arrays.asList("Image.jpg");
        List<OneFile> children = folder.getChildFiles();

        Assertions.assertEquals(1, children.size());

        for (OneFile child : children) {
            Assertions.assertTrue(expectedChildren.contains(child.getName()));
        }

        for (String expectedChild : expectedChildren) {
            boolean found = false;
            for (OneFile child : children) {
                if (child.getName().equals(expectedChild)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found);
        }
    }

    @Test
    public void testGetChildren() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
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

        Assertions.assertEquals(expectedFolder, folderCount);
        Assertions.assertEquals(expectedFiles, fileCount);
    }

    @Test
    public void testCreateAndDeleteFolder() throws IOException, OneDriveException {
        String folderName = "TestFolder";
        String path = "/IntegrationTesting/FolderForFolderCreation";

        OneDriveSDK api = this.connect();
        OneFolder targetFolder = api.getFolderByPath(path);

        int folderCount = targetFolder.getChildCount();

        OneFolder createdFolder = targetFolder.createFolder(folderName);
        OneFolder secondFolder = targetFolder.createFolder(folderName,ConflictBehavior.RENAME);

        targetFolder = targetFolder.refresh();
        boolean rightFolderCount = (folderCount + 2) == targetFolder.getChildCount();

        Assertions.assertTrue(rightFolderCount);
        Assertions.assertEquals(folderName, createdFolder.getName());
        Assertions.assertEquals(folderName+" 1", secondFolder.getName());

        //delete folder (cleanup)
        if (rightFolderCount) {
            createdFolder.delete();
            secondFolder.delete();
            targetFolder = targetFolder.refresh();
            Assertions.assertEquals(folderCount, targetFolder.getChildCount());
        }
    }
    @Test
    public void testRefresh() throws IOException, OneDriveException {
        String folderName = "TestFolder";
        String path = "/IntegrationTesting/FolderForFolderCreation";

        OneDriveSDK api = this.connect();
        OneFolder targetFolder = api.getFolderByPath(path);

        OneFolder createdFolder = targetFolder.createFolder(folderName);

        OneFolder refreshFolder = targetFolder.refresh();

        Assertions.assertTrue(refreshFolder.getLastRefresh() > targetFolder.getLastRefresh());

        Assertions.assertEquals(targetFolder.getId(), refreshFolder.getId());
        Assertions.assertEquals(targetFolder.getName(), refreshFolder.getName());
        Assertions.assertFalse(targetFolder.getChildCount() == refreshFolder.getChildCount());

        OneFolder refetchFolder = api.getFolderByPath(path);

        Assertions.assertEquals(refetchFolder.getId(), refreshFolder.getId());
        Assertions.assertEquals(refetchFolder.getName(), refreshFolder.getName());
        Assertions.assertTrue(refetchFolder.getChildCount() == refreshFolder.getChildCount());

        Assertions.assertTrue(refreshFolder.getLastRefresh() < refetchFolder.getLastRefresh());
        Assertions.assertTrue(refetchFolder != targetFolder && refreshFolder != targetFolder);

        createdFolder.delete();
    }

    @Test
    public void testDeleteFile() throws Exception {
        OneDriveSDK api = this.connect();
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
            Assertions.assertEquals(childCount - 1, targetFolder.getChildCount());
        } else {
            Assertions.fail("file with name '" + testFileName + "' not found");
        }
    }

    @Test
    public void testCopyFile() throws IOException, OneDriveException, ParseException, InterruptedException {
        OneDriveSDK api = this.connect();
        OneFile file = api.getFileByPath("/IntegrationTesting/FolderForMoveAndCopy/Image.jpg");
        OneFolder targetFolder = api.getFolderByPath("/IntegrationTesting/FolderForMoveAndCopy/CopyTarget");

        int itemCount = targetFolder.getChildCount();

        OneFile newFile = file.copy(targetFolder);
        targetFolder = targetFolder.refresh();

        if (newFile != null) {
            newFile.delete();
        }

        Assertions.assertEquals(itemCount + 1, targetFolder.getChildCount());
        Assertions.assertEquals(newFile.getName(), file.getName());
    }

    @Test
    public void testMoveFile() throws IOException, OneDriveException, ParseException, InterruptedException {
        OneDriveSDK api = this.connect();
        OneFile file = api.getFileByPath("/IntegrationTesting/FolderForMoveAndCopy/ImageForMove.jpg");
        OneFolder sourceFolder = file.getParentFolder();
        OneFolder targetFolder = api.getFolderByPath("/IntegrationTesting/FolderForMoveAndCopy/MoveTarget");

        int sourceItemCount = sourceFolder.getChildCount();
        int targetItemCount = targetFolder.getChildCount();

        Assertions.assertEquals(sourceFolder.getId(), file.getParentFolder().getId());

        OneFile newFile = file.move(targetFolder);
        file = file.refresh();

        Assertions.assertEquals(targetFolder.getId(), file.getParentFolder().getId());

        targetFolder = targetFolder.refresh();
        sourceFolder = sourceFolder.refresh();

        newFile.move(sourceFolder);

        Assertions.assertEquals(sourceItemCount - 1, sourceFolder.getChildCount());
        Assertions.assertEquals(targetItemCount + 1, targetFolder.getChildCount());
        Assertions.assertEquals(newFile.getName(), file.getName());
    }

    @Test
    public void testParentFolder() throws IOException, OneDriveException {
        OneDriveSDK api = this.connect();
        OneFolder folder = api.getFolderByPath("IntegrationTesting");
        Assertions.assertEquals("root", folder.getParentFolder().getName());
    }

    @Test
    public void testFactory() {
        Assertions.assertNotNull(new OneDriveFactory());
        Assertions.assertNotNull(OneDriveFactory.createOneDriveSDK(OneDriveCredentials.getClientId(), OneDriveCredentials.getClientSecret(), OneDriveScope.READWRITE));
        Assertions.assertNotNull(OneDriveFactory.createOneDriveSDK(OneDriveCredentials.getClientId(), OneDriveCredentials.getClientSecret(),"",OneDriveScope.READWRITE));
        Assertions.assertNotNull(OneDriveFactory.createOneDriveSDK(OneDriveCredentials.getClientId(), OneDriveCredentials.getClientSecret(), "", new ExceptionEventHandler() {
            @Override
            public void handle(Exception e) {

            }

            @Override
            public void handle(Object src, Exception e) {

            }
        }, OneDriveScope.READWRITE));
    }

    private OneDriveSDK connect(){
        return TestSDKFactory.getInstance();
    }
}
