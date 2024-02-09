package de.tuberlin;

import java.util.List;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.common.OneItemType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Created by Andi on 10.05.2015.
 */
public class OneItemTest {

    @Test
    public void testNullJsonParseItems() {
        assertThrows(NullPointerException.class, () -> OneItem.parseItemsFromJson(null));
    }

    @Test
    public void testNullJsonParse() {
        assertThrows(NullPointerException.class, () -> OneItem.fromJSON(null));
    }

    @Test
    public void testParseEmptyOneItem() {
        String json = "{}";
        OneItem folder = null;
        try {
            folder = OneItem.fromJSON(json);
        } catch (OneDriveException e) {
            fail();
        }

        assertEquals("", folder.getId());
        assertEquals("", folder.getName());
        assertEquals(0, folder.getCreatedBy().size());
        assertEquals(0, folder.getCreatedDateTime());
        assertEquals(0, folder.getLastModifiedBy().size());
        assertEquals(0, folder.getLastModifiedDateTime());
        assertEquals("", folder.getCTag());
        assertEquals("", folder.getETag());
        assertEquals(0, folder.getSize());
        assertEquals("", folder.getWebUrl());
    }

    @Test
    public void testParseEmptyOneItems() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json);
        } catch (Exception e) {
            fail();
        }
        assertEquals(2, items.size());
    }

    @Test
    public void testParseEmptyOneFiles() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.FILE);
        } catch (Exception e) {
            fail();
        }
        assertEquals(1, items.size());
    }

    @Test
    public void testParseEmptyOneFolder() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.FOLDER);
        } catch (Exception e) {
            fail();
        }
        assertEquals(1, items.size());
    }

    @Test
    public void testParseCorruptItems() {
        String json = "{\"val\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.ALL);
            fail();
        } catch (OneDriveException e) {

        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testParseDate() {
        String json = "{\"createdDateTime\":\"2015-05-01T10:30:19.55Z\"}";
        OneItem item = null;
        try {
            item = OneItem.fromJSON(json);
        } catch (Exception e) {
            fail();
        }
        assertEquals(1430476219L, item.getCreatedDateTime());

        json = "{\"createdDateTime\":\"2015-05-01T\"}";
        try {
            item = OneItem.fromJSON(json);
        } catch (Exception e) {
            fail();
        }
        assertEquals(0L, item.getCreatedDateTime());
    }

    @Test
    public void setNullApi() {
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");
        } catch (Exception e) {
            fail();
        }
        try {
            item.setApi(null);
            fail();
        } catch (OneDriveException e) {
        }
    }

    @Test
    public void testSetApi() {
        OneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");
        } catch (Exception e){
            fail();
        }
        try {
            item.setApi(api);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void testDeleteItem() {
        OneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");

            Mockito.doReturn(true).when(api).deleteItem(item);
            item.setApi(api);
            item.delete();
        } catch (Exception e){
            fail();
        }
    }
}
