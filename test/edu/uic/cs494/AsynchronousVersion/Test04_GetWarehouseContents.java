package edu.uic.cs494.AsynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test04_GetWarehouseContents {
    @Test
    public void testUsingOwnSet() {
        Warehouse w = Warehouse.createWarehouse();
        int size = 10;
        Shelf s = w.createShelf(size).startThread();

        Item[] items = new Item[size];

        for (int i = 0 ; i < size ; i++) {
            items[i] = w.createItem(Test01_AddItems.ITEM_NAME + i);
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(items[i]);
            w.addItems(s, toAdd);

            Assert.assertNotSame(toAdd, w.getContents());

            toAdd.clear();

            Set<Item> expected = new HashSet<>();
            for (int j = 0 ; j <= i ; j++)
                expected.add(items[j]);

            Assert.assertEquals(expected, w.getContents());

            w.getContents().clear();
            w.getContents(s).clear();

            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testCapacityAdd() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(1).startThread();

        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            toAdd.add(i2);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            w.addItems(s, toAdd);
            toAdd.clear();
            toAdd.add(i2);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());

            w.getContents().clear();

            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testCapacitySameDescription() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        testCapacitySameDescription(sequentialIndexes);
        testCapacitySameDescription(shuffledIndexesList);
    }

    public void testCapacitySameDescription(List<Integer> indexes) {
        int size = indexes.size();
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(size).startThread();

        Item[] items = new Item[size];
        Set<Item> toAdd = new HashSet<>();
        Set<Item> expected = new HashSet<>();

        for (int i : indexes) {
            items[i] = w.createItem(Test01_AddItems.ITEM_NAME + i);
            toAdd.add(items[i]);
            expected.add(items[i]);
        }

        w.addItems(s, toAdd);

        Assert.assertEquals(size, expected.size());
        Assert.assertEquals(expected, w.getContents());
        w.getContents().clear();
        Assert.assertEquals(size, expected.size());
        Assert.assertEquals(expected, w.getContents());

        Assert.assertEquals(size, w.getContents().size());
        w.getContents(s).clear();
        Assert.assertEquals(size, w.getContents().size());
    }

    @Test
    public void testCapacityDuplicatedItem() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(10).startThread();

        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            w.addItems(s, toAdd);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            toAdd.add(i2);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i2);
            w.addItems(s, toAdd);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1,i2}));
            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testContentsRemoveFromShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(2).startThread();

        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);
            w.addItems(s, items);
            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
        }

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            w.addItems(s, items);
            items.clear();
            items.add(i2);
            w.addItems(s, items);


            items.clear();
            items.add(i1);
            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i2}));
            Assert.assertEquals(expected, w.getContents());

            items.clear();
            items.add(i2);
            w.removeItems(s, items);

            expected.clear();
            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testContentsRemoveFromEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(2).startThread();

        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
        }

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            w.addItems(s, items);
            w.removeItems(s, items);
            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());

            items.clear();
            items.add(i1);
            w.removeItems(s, items);
            Assert.assertEquals(expected, w.getContents());

            items.clear();
            items.add(i2);
            w.removeItems(s, items);
            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testContenstsRemoveItemNotInShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(1).startThread();

        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            w.addItems(s, items);

            items.clear();
            items.add(i2);
            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());

            w.getContents().clear();

            Assert.assertEquals(expected, w.getContents());
        }
    }
    @Test
    public void testRemoveFromShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2).startThread();
        Shelf s2 = w.createShelf(2).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);
            w.addItems(s1, items);
            w.removeItems(s2, items);
            w.removeItems(s1, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
        }

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            w.addItems(s1, items);
            items.clear();
            items.add(i2);
            w.addItems(s2, items);


            items.clear();
            items.add(i1);
            w.removeItems(s1, items);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i2}));
            Assert.assertEquals(expected, w.getContents());

            items.clear();
            items.add(i2);
            w.removeItems(s2, items);

            expected.clear();
            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testRemoveFromEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2).startThread();
        Shelf s2 = w.createShelf(2).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            w.removeItems(s1, items);
            w.removeItems(s2, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
        }

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            w.addItems(s1, items);
            w.removeItems(s1, items);
            w.removeItems(s2, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());

            items.clear();
            items.add(i1);
            w.removeItems(s1, items);
            w.removeItems(s2, items);
            Assert.assertEquals(expected, w.getContents());

            items.clear();
            items.add(i2);
            w.removeItems(s1, items);
            w.removeItems(s2, items);
            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testRemoveItemNotInShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(1).startThread();
        Shelf s2 = w.createShelf(1).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            w.addItems(s1, items);

            items.clear();
            items.add(i2);
            w.removeItems(s2, items);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());

        }
    }
}