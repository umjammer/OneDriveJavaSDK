package de.tuberlin.onedrivesdk.uploadFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.common.TestSDKFactory;
import de.tuberlin.onedrivesdk.file.OneFile;
import de.tuberlin.onedrivesdk.folder.OneFolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ConcreteOneUploadFileIntegrationTest {

    private OneFile uploadedFile;

    @Test
    public void simpleUploadTest() throws Exception {
        OneDriveSDK api = TestSDKFactory.getInstance();
        File file = new File("src/test/resources/uploadTest.jpg");
        OneFolder folder = api.getRootFolder();
        OneUploadFile upload = folder.uploadFile(file);
        //Future<OneFile> futureUpload = executor.submit(upload);
        uploadedFile = upload.startUpload();
        System.out.println(uploadedFile.toString());
        assertNotNull(uploadedFile);
        assertEquals("uploadTest.jpg", uploadedFile.getName());
        System.out.println(uploadedFile.getId());
    }

    @AfterEach
    public void removeTestFile() throws Exception {
        if(uploadedFile!=null) {
            uploadedFile.delete();
            uploadedFile=null;
        }
    }

    public static Field getUnaccessibleField(String fieldName, Class<?> clazz) throws Exception {

        Field privateField = clazz.getDeclaredField(fieldName);

        privateField.setAccessible(true);

        return privateField;
    }
}
