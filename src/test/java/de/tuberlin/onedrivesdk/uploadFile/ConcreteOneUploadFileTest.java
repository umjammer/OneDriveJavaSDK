package de.tuberlin.onedrivesdk.uploadFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.folder.ConcreteOneFolder;
import de.tuberlin.onedrivesdk.folder.OneFolder;


public class ConcreteOneUploadFileTest {

    File fileToUploadPath;

    OneDriveSDK mockApi;

    @Test
    public void getNextRange() throws Exception {

        UploadSession upSession = getEmptyUploadSession();

        String[] nextRanges = {"1435-","16843-65786","547547-65756"};
        getUnaccessableField("nextExpectedRanges", UploadSession.class).set(upSession, nextRanges);

        assertEquals(1435L, upSession.getNextRange());
    }

    @Test
    public void testCreateUploadSession() throws Exception {

        UploadSession upSession = getEmptyUploadSession();


        getUnaccessableField("uploadUrl", UploadSession.class).set(upSession,
                "asifsdiurt");

        ConcreteOneFolder folder = makeMockFolder();
        when(
             mockApi.createUploadSession(any(OneFolder.class),
                                         any(String.class))).thenReturn(upSession);
        OneUploadFile upload = new ConcreteOneUploadFile(folder,
                                                                 fileToUploadPath, mockApi);
        Mockito.verify(mockApi).createUploadSession(folder,
                                                    fileToUploadPath.getName());
        assertEquals("asifsdiurt",
                     getUnaccessableField("uploadUrl", ConcreteOneUploadFile.class)
                     .get(upload));

    }

    public static ConcreteOneFolder makeMockFolder() {
        ConcreteOneFolder folder = mock(ConcreteOneFolder.class);
        when(folder.getId()).thenReturn("aiusgtffgso8745whfirstgu");
        return folder;
    }

    @AfterEach
    public void removeTestFile() {
        fileToUploadPath.delete();
    }

    @BeforeEach
    public void createTestFile() throws IOException {
        fileToUploadPath = File.createTempFile("TestOneSDKFile", "txt");
    }

    @BeforeEach
    public void createAPIMock() {
        mockApi = mock(OneDriveSDK.class);
    }

    public static Field getUnaccessableField(String fieldName, Class<?> clazz) throws Exception {

        Field privateField = clazz.getDeclaredField(fieldName);

        privateField.setAccessible(true);

        return privateField;
    }

    private static UploadSession getEmptyUploadSession() throws Exception {
        // get constructor that takes a String as argument
        @SuppressWarnings("unchecked")
        Constructor<UploadSession> constructor = (Constructor<UploadSession>) UploadSession.class
                .getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        UploadSession upSession = constructor.newInstance();

        return upSession;
    }
}
