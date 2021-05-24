package edu.uic.cs494.CustomReaderWritterLock;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test02_SingleShelfContents {
    @Test
    public void testUsingOwnSet() {
        Warehouse w = Warehouse.createWarehouse();
        int size = 10;
        Shelf s = w.createShelf(size);
        Item[] items = new Item[size];

        for (int i = 0 ; i < size ; i++) {
            items[i] = w.createItem(Test01_SingleShelfLimits.ITEM_NAME + i);
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(items[i]);
            w.addItems(s, toAdd);

            Assert.assertNotSame(toAdd, w.getContents());
            Assert.assertNotSame(toAdd, w.getContents(s));

            toAdd.clear();

            Set<Item> expected = new HashSet<>();
            for (int j = 0 ; j <= i ; j++)
                expected.add(items[j]);

            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));

            w.getContents().clear();
            w.getContents(s).clear();

            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
        }
    }

    @Test
    public void testCapacityAdd() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(1);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            toAdd.add(i2);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
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
            Assert.assertEquals(expected, w.getContents(s));

            w.getContents().clear();
            w.getContents(s).clear();

            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
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
        Shelf s = w.createShelf(size);
        Item[] items = new Item[size];
        Set<Item> toAdd = new HashSet<>();
        Set<Item> expected = new HashSet<>();

        for (int i : indexes) {
            items[i] = w.createItem(Test01_SingleShelfLimits.ITEM_NAME + i);
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
        Assert.assertEquals(expected, w.getContents(s));
        w.getContents(s).clear();
        Assert.assertEquals(size, w.getContents().size());
        Assert.assertEquals(expected, w.getContents(s));
    }

    @Test
    public void testCapacityDuplicatedItem() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(10);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            w.addItems(s, toAdd);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            toAdd.add(i2);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i2);
            w.addItems(s, toAdd);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1,i2}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
        }
    }

    @Test
    public void testRemoveFromShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(2);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);
            w.addItems(s, items);
            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
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
            Assert.assertEquals(expected, w.getContents(s));

            items.clear();
            items.add(i2);
            w.removeItems(s, items);

            expected.clear();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
        }
    }

    @Test
    public void testRemoveFromEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(2);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
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
            Assert.assertEquals(expected, w.getContents(s));

            items.clear();
            items.add(i1);
            w.removeItems(s, items);
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));

            items.clear();
            items.add(i2);
            w.removeItems(s, items);
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
        }
    }

    @Test
    public void testRemoveItemNotInShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(1);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            w.addItems(s, items);

            items.clear();
            items.add(i2);
            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));

            w.getContents().clear();
            w.getContents(s).clear();

            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s));
        }
    }
}
