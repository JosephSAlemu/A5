package org.example.Amazon;

import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.RegularCost;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class AmazonUnitTest {

    @Test
    @DisplayName("specification-based")
    void deliveryPriceNoItemsTest()
    {
        DeliveryPrice deliveryPrice = new DeliveryPrice();
        List<Item> emptyItems = new ArrayList<>();
        assertThat(deliveryPrice.priceToAggregate(emptyItems)).isEqualTo(0);
    }

    @Test
    @DisplayName("specification-based")
    void deliveryPriceOneItemTest()
    {
        DeliveryPrice deliveryPrice = new DeliveryPrice();

        Item buggedItem1 = new Item(ItemType.OTHER, "coke", 500000, 2.50);
        Item buggedItem2 = new Item(ItemType.OTHER, "chips", 0, 3.50);
        Item normalItem = new Item(ItemType.OTHER, "chocolate", 1, 5.50);

        List<Item> bug1 = List.of(buggedItem1);
        List<Item> bug2 = List.of(buggedItem2);
        List<Item> normal = List.of(normalItem);

        // Bug in the code. We are getting 5 due to there being "one" item. However, there are technically 500,000 items.
        assertThat(deliveryPrice.priceToAggregate(bug1)).isEqualTo(5);
        // Bug in the code. We have a quantity of 0 for the item. We should not be allowed to do so.
        assertThat(deliveryPrice.priceToAggregate(bug2)).isEqualTo(5);

        assertThat(deliveryPrice.priceToAggregate(normal)).isEqualTo(5);
    }


    @Test
    @DisplayName("specification-based")
    void deliveryPriceMultipleItemsTest()
    {
        DeliveryPrice deliveryPrice = new DeliveryPrice();

        Item buggedItem1 = new Item(ItemType.OTHER, "coke", 500000, 2.50);
        Item buggedItem2 = new Item(ItemType.OTHER, "chips", 0, 3.50);
        Item normalItem1 = new Item(ItemType.OTHER, "chocolate", 1, 5.50);
        Item normalItem2 = new Item(ItemType.ELECTRONIC, "pc", 1, 450.50);
        Item normalItem3 = new Item(ItemType.OTHER, "eggs", 1, 13);
        Item normalItem4 = new Item(ItemType.OTHER, "milk", 1, 10);
        Item normalItem5 = new Item(ItemType.ELECTRONIC, "nintendo switch", 1, 450.50);
        Item normalItem6 = new Item(ItemType.OTHER, "sugar", 1, 450.50);
        Item normalItem7 = new Item(ItemType.ELECTRONIC, "blender", 1, 125);
        Item normalItem8 = new Item(ItemType.ELECTRONIC, "charger", 1, 50.45);
        Item normalItem9 = new Item(ItemType.OTHER, "spatula", 1, 50.45);

        List<Item> threeItems = List.of(buggedItem1, normalItem3, normalItem8);
        List<Item> fourItems = List.of(buggedItem2, normalItem1, normalItem2, normalItem6);
        List<Item> tenItems = List.of(buggedItem2, normalItem1, normalItem2, normalItem3, normalItem4, normalItem5, normalItem6, normalItem7, normalItem8, normalItem9);
        List<Item> elevenItems = List.of(buggedItem1, buggedItem2, normalItem1, normalItem2, normalItem3, normalItem4, normalItem5, normalItem6, normalItem7, normalItem8, normalItem9);

        //I explained the bugs in the previous test.
        assertThat(deliveryPrice.priceToAggregate(threeItems)).isEqualTo(5);
        assertThat(deliveryPrice.priceToAggregate(fourItems)).isEqualTo(12.5);
        assertThat(deliveryPrice.priceToAggregate(tenItems)).isEqualTo(12.5);
        assertThat(deliveryPrice.priceToAggregate(elevenItems)).isEqualTo(20);
    }

    @Test
    @DisplayName("specification-based")
    void extraCostForElectronicsNoElectronicsTest()
    {
        ExtraCostForElectronics extraCost = new ExtraCostForElectronics();

        Item Item1 = new Item(ItemType.OTHER, "chocolate", 1, 5.50);
        Item Item2 = new Item(null, "eggs", 1, 13);
        Item Item3 = new Item(ItemType.OTHER, "milk", 1, 10);

        List<Item> list = List.of(Item1, Item2, Item3);
        List<Item> emptyList = new ArrayList<>();

        assertThat(extraCost.priceToAggregate(list)).isEqualTo(0);
        assertThat(extraCost.priceToAggregate(emptyList)).isEqualTo(0);
    }

    @Test
    @DisplayName("specification-based")
    void extraCostForElectronicsWithElectronicsTest()
    {
        ExtraCostForElectronics extraCost = new ExtraCostForElectronics();

        Item Item1 = new Item(ItemType.ELECTRONIC, "pc", 1, 545.50);
        Item Item2 = new Item(ItemType.OTHER, "milk", 1, 10);

        List<Item> list = List.of(Item1, Item2);

        assertThat(extraCost.priceToAggregate(list)).isEqualTo(7.5);
    }

    @Test
    @DisplayName("specification-based")
    void regularCostPriceAndQuantityLessThanOneTest()
    {
        RegularCost regularCost = new RegularCost();

        Item Item1 = new Item(ItemType.ELECTRONIC, "pc", 0, 0);
        Item Item2 = new Item(ItemType.OTHER, "milk", -5, -5);

        List<Item> list = List.of(Item1, Item2);

        // Bug, the code should not be able to allow prices or quantities less than one. An exception should be thrown.
        assertThat(regularCost.priceToAggregate(list)).isEqualTo(25);
    }

    @Test
    @DisplayName("specification-based")
    void regularCostPriceLessThanOneTest()
    {
        RegularCost regularCost = new RegularCost();

        Item Item1 = new Item(ItemType.ELECTRONIC, "pc", 1, 0);
        Item Item2 = new Item(ItemType.OTHER, "chocolate", 3, -10);

        List<Item> list = List.of(Item1, Item2);

        // Bug, the code should not be able to allow prices or quantities less than one. An exception should be thrown.
        assertThat(regularCost.priceToAggregate(list)).isEqualTo(-30);
    }

    @Test
    @DisplayName("specification-based")
    void regularCostQuantityLessThanOneTest()
    {
        RegularCost regularCost = new RegularCost();

        Item Item1 = new Item(ItemType.OTHER, "eggs", 0, 4);
        Item Item2 = new Item(ItemType.OTHER, "milk", -20, 3);

        List<Item> list = List.of(Item1, Item2);

        // Bug, the code should not be able to allow prices or quantities less than one. An exception should be thrown.
        assertThat(regularCost.priceToAggregate(list)).isEqualTo(-60);
    }

    @Test
    @DisplayName("specification-based")
    void regularCostPriceAndQuantityGreaterThanOrEqualToOneTest()
    {
        RegularCost regularCost = new RegularCost();

        Item Item1 = new Item(ItemType.OTHER, "eggs", 1, 3);
        Item Item2 = new Item(ItemType.OTHER, "milk", 10, 3);

        List<Item> list = List.of(Item1, Item2);

        assertThat(regularCost.priceToAggregate(list)).isEqualTo(33);

    }
    @Test
    @DisplayName("structural-based")
    void resetDatabaseTest()
    {
        Database db = new Database();
        Connection connection1 = db.getConnection();
        db.resetDatabase();
        Connection connection2 = db.getConnection();
        assertThat(connection1).isNotEqualTo(connection2);
    }

    @Test
    @DisplayName("structural-based")
    void closedDatabaseTest() {
        Database db = new Database();
        Connection originalConnection = db.getConnection();
        db.close();
        db.close();
        Connection connection1 = db.getConnection();

        assertThat(connection1).isEqualTo(null);
        assertThrows(RuntimeException.class, () -> {
            db.withSql( () -> {
                originalConnection.prepareStatement("SELECT * FROM shoppingcart");
                return null;
            });
        });
    }

    @Test
    @DisplayName("specification-based")
    void shoppingCartAdaptorZeroItemsTest()
    {
        List<Item> items = new ArrayList<>();
        Database db = mock(Database.class);
        ShoppingCartAdaptor adaptor = new ShoppingCartAdaptor(db);

        when(db.withSql(any())).thenReturn(items);
        adaptor.add(null);

        verify(db, times(1)).withSql(any());

        assertThat(adaptor.getItems()).isEqualTo(items);
    }

    @Test
    @DisplayName("specification-based")
    void shoppingCartAdaptorZeroSizeTest()
    {
        Database db = mock(Database.class);
        ShoppingCartAdaptor adaptor = new ShoppingCartAdaptor(db);

        when(db.withSql(any())).thenReturn(0);
        adaptor.add(null);

        verify(db, times(1)).withSql(any());

        assertThat(adaptor.numberOfItems()).isEqualTo(0);
    }

    @Test
    @DisplayName("specification-based")
    void shoppingCartAdaptorGreaterThanZeroItemsTest()
    {
        List<Item> items;
        Database db = mock(Database.class);
        ShoppingCartAdaptor adaptor = new ShoppingCartAdaptor(db);

        Item Item1 = new Item(ItemType.OTHER, "eggs", 1, 3);
        Item Item2 = new Item(ItemType.OTHER, "milk", 10, 3);

        items = List.of(Item1, Item2);

        when(db.withSql(any())).thenReturn(items);
        adaptor.add(Item1);
        adaptor.add(Item2);

        verify(db, times(2)).withSql(any());

        assertThat(adaptor.getItems()).isEqualTo(items);
    }

    @Test
    @DisplayName("specification-based")
    void shoppingCartAdaptorGreaterThanZeroSizeTest()
    {
        List<Item> items;
        Database db = mock(Database.class);
        ShoppingCartAdaptor adaptor = new ShoppingCartAdaptor(db);

        Item Item1 = new Item(ItemType.OTHER, "eggs", 1, 3);
        Item Item2 = new Item(ItemType.OTHER, "milk", 10, 3);

        items = List.of(Item1, Item2);

        when(db.withSql(any())).thenReturn(items.size());
        adaptor.add(Item1);
        adaptor.add(Item2);

        verify(db, times(2)).withSql(any());

        assertThat(adaptor.numberOfItems()).isEqualTo(items.size());
    }



}
