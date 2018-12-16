package de.tuberlin;

import de.tuberlin.onedrivesdk.common.OneItem;
import de.tuberlin.onedrivesdk.OneDriveException;
import de.tuberlin.onedrivesdk.common.ConcreteOneDriveSDK;
import de.tuberlin.onedrivesdk.common.OneItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Created by Andi on 10.05.2015.
 */
public class OneItemTest {

    @Test
    public void testNullJsonParseItems() {
        try {
            OneItem.parseItemsFromJson(null);
            Assertions.fail();
        } catch (org.json.simple.parser.ParseException e) {
        } catch (OneDriveException e) {
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testNullJsonParse() {

        try {
            OneItem.fromJSON(null);
            Assertions.fail();
        } catch (org.json.simple.parser.ParseException e) {
        } catch (OneDriveException e) {
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void testParseEmptyOneItem() {
        String json = "{}";
        OneItem folder = null;
        try {
            folder = OneItem.fromJSON(json);
        } catch (org.json.simple.parser.ParseException e) {
            Assertions.fail();
        } catch (OneDriveException e) {
            Assertions.fail();
        }

        Assertions.assertEquals("", folder.getId());
        Assertions.assertEquals("", folder.getName());
        Assertions.assertEquals(0, folder.getCreatedBy().size());
        Assertions.assertEquals(0, folder.getCreatedDateTime());
        Assertions.assertEquals(0, folder.getLastModifiedBy().size());
        Assertions.assertEquals(0, folder.getLastModifiedDateTime());
        Assertions.assertEquals("", folder.getCTag());
        Assertions.assertEquals("", folder.getETag());
        Assertions.assertEquals(0, folder.getSize());
        Assertions.assertEquals("", folder.getWebUrl());
    }

    @Test
    public void testParseEmptyOneItems() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(2, items.size());
    }

    @Test
    public void testParseEmptyOneFiles() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.FILE);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(1, items.size());
    }

    @Test
    public void testParseEmptyOneFolder() {
        String json = "{\"value\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.FOLDER);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(1, items.size());
    }

    @Test
    public void testParseCorruptItems() {
        String json = "{\"val\":[{},{\"file\":{}}]}";
        List<OneItem> items = null;
        try {
            items = OneItem.parseItemsFromJson(json, OneItemType.ALL);
            Assertions.fail();
        } catch (OneDriveException | org.json.simple.parser.ParseException e) {

        } catch (Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testParseDate() {
        String json = "{\"createdDateTime\":\"2015-05-01T10:30:19.55Z\"}";
        OneItem item = null;
        try {
            item = OneItem.fromJSON(json);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(1430469019L, item.getCreatedDateTime());

        json = "{\"createdDateTime\":\"2015-05-01T\"}";
        try {
            item = OneItem.fromJSON(json);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertEquals(0L, item.getCreatedDateTime());
    }

    @Test
    public void setNullApi() {
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");
        } catch (Exception e) {
            Assertions.fail();
        }
        try {
            item.setApi(null);
            Assertions.fail();
        } catch (OneDriveException e) {
        }
    }

    @Test
    public void testSetApi() {
        ConcreteOneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");
        } catch (Exception e){
            Assertions.fail();
        }
        try {
            item.setApi(api);
        } catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    public void testDeleteItem() {
        ConcreteOneDriveSDK api = Mockito.mock(ConcreteOneDriveSDK.class);
        OneItem item = null;
        try {
            item = OneItem.fromJSON("{}");

            Mockito.doReturn(true).when(api).deleteItem(item);
            item.setApi(api);
            item.delete();
        } catch (Exception e){
            Assertions.fail();
        }
    }
}
