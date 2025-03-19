package de.tuberlin;

import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.OneDriveSDK;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.common.OneItemType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Created by Andi on 10.05.2015.
 */
class OneItemTest {

    @Test
    void testNullJsonParseItems() {
        assertThrows(NullPointerException.class, () -> OneItem.parseItemsFromJson(null));
    }

    @Test
    void testNullJsonParse() {
        assertThrows(NullPointerException.class, () -> OneItem.fromJSON(null));
    }

    @Test
    void testParseEmptyOneItem() throws Exception {
        String json = "{}";
        OneItem folder = OneItem.fromJSON(json);

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
    void testParseEmptyOneItems() throws Exception {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = OneItem.parseItemsFromJson(json);
        assertEquals(2, items.size());
    }

    @Test
    void testParseEmptyOneFiles() throws Exception {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = OneItem.parseItemsFromJson(json, OneItemType.FILE);
        assertEquals(1, items.size());
    }

    @Test
    void testParseEmptyOneFolder() throws Exception {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = OneItem.parseItemsFromJson(json, OneItemType.FOLDER);
        assertEquals(1, items.size());
    }

    @Test
    void testParseCorruptItems() {
        String json = "{\"val\":[{},{\"file\":{}}]}";
        assertThrows(OneDriveException.class, () -> {
            OneItem.parseItemsFromJson(json, OneItemType.ALL);
        });
    }

    @Test
    void testParseDate() throws Exception {
        String json = "{\"createdDateTime\":\"2015-05-01T10:30:19.55Z\"}";
        OneItem item = OneItem.fromJSON(json);
        assertEquals(1430476219L, item.getCreatedDateTime());

        json = "{\"createdDateTime\":\"2015-05-01T\"}";
        item = OneItem.fromJSON(json);
        assertEquals(1430438400L, item.getCreatedDateTime());
    }

    @Test
    void setNullApi() throws Exception {
        OneItem item = OneItem.fromJSON("{}");
        assertThrows(OneDriveException.class, () -> {
            item.setApi(null);
        });
    }

    @Test
    void testSetApi() throws Exception {
        OneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = OneItem.fromJSON("{}");
        item.setApi(api);
    }

    @Test
    void testDeleteItem() throws Exception {
        OneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = OneItem.fromJSON("{}");

        Mockito.doReturn(true).when(api).deleteItem(item);
        item.setApi(api);
        item.delete();
    }
}
