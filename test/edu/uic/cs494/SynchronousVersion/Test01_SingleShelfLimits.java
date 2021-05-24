package edu.uic.cs494.SynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test01_SingleShelfLimits {
    /*default*/ final static String ITEM_NAME = "item";
    /*default*/ final static String ANOTHER_ITEM_NAME = "anotherItem";
    /*default*/ final static String LAPTOP_ITEM_NAME = "laptop";
    /*default*/ final static String DESKTOP_ITEM_NAME = "desktop";

    @Test
    public void testItemIdentity() {
        Warehouse w = Warehouse.createWarehouse();

        int size = 10;
        Item[] items = new Item[size];

        Set<Item> toAdd = new HashSet<>();
        for (int i = 0 ; i < size ; i++) {
            items[i] = w.createItem(ITEM_NAME);
            toAdd.add(items[i]);
        }

        Assert.assertEquals(size, toAdd.size());
    }

    @Test
    public void testMaxCapacity() {
        Warehouse w = Warehouse.createWarehouse();
        int size = 10;
        int test = 20;
        Shelf s = w.createShelf(size);

        for (int i = 0 ; i < test ; i++) {
            Item item = w.createItem(ITEM_NAME + i);
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(item);

            if (i < size)
                Assert.assertTrue(w.addItems(s, toAdd));
            else
                Assert.assertFalse(w.addItems(s, toAdd));
        }
    }

    @Test
    public void testItemAlreadyInShelf() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        testItemAlreadyInShelf(sequentialIndexes);
        testItemAlreadyInShelf(shuffledIndexesList);
    }

    private void testItemAlreadyInShelf(List<Integer> indexes) {
        int size = indexes.size();
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(size);

        Item[] items = new Item[size];
        for (int i = 0 ; i < size ; i++) {
            items[i] = w.createItem(ITEM_NAME+i);
        }

        for (int i : indexes) {
            if (i > 0) {
                Set<Item> toAdd = new HashSet<>();
                toAdd.add(items[i]);
                toAdd.add(items[i-1]);
                Assert.assertFalse(w.addItems(s, toAdd));
                Assert.assertFalse(w.addItems(s, new HashSet<>(Arrays.asList(new Item[]{items[i], items[i-1]}))));
            }

            {
                Set<Item> toAdd = new HashSet<>();
                toAdd.add(items[i]);
                Assert.assertTrue(w.addItems(s, toAdd));
                Assert.assertFalse(w.addItems(s, toAdd));
                Assert.assertFalse(w.addItems(s, new HashSet<>(Arrays.asList(new Item[]{items[i]}))));
            }

            {
                Set<Item> toAdd = new HashSet<>();
                toAdd.add(items[i]);
                Item anotherItem = w.createItem(ANOTHER_ITEM_NAME + i);
                toAdd.add(anotherItem);
                Assert.assertFalse(w.addItems(s, toAdd));
                Assert.assertFalse(w.addItems(s, new HashSet<>(Arrays.asList(new Item[]{items[i], anotherItem}))));
            }
        }
    }

    @Test
    public void testRemoveFromShelf() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList = IntStream.range(0, size).boxed().collect(Collectors.toList());

        testRemoveFromShelf(sequentialIndexes);
        testRemoveFromShelf(shuffledIndexesList);
    }

    public void testRemoveFromShelf(List<Integer> indexes) {

        Warehouse w = Warehouse.createWarehouse();
        int size = indexes.size();
        Shelf s = w.createShelf(size);

        Item[] items = new Item[size];
        for (int i = 0 ; i < size ; i++) {
            items[i] = w.createItem(ITEM_NAME + i);
        }

        for (int i : indexes) {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(items[i]);
            Assert.assertTrue(w.addItems(s, toAdd));
            Assert.assertTrue(w.removeItems(s, toAdd));
        }

        {
            Set<Item> toAdd = new HashSet<>();
            for (int i : indexes) {
                toAdd.add(items[i]);
            }

            Assert.assertTrue(w.addItems(s, toAdd));
            Assert.assertTrue(w.removeItems(s, toAdd));
        }

        {
            Set<Item> toAdd = new HashSet<>();
            for (int i : indexes) {
                toAdd.add(items[i]);
            }

            Assert.assertTrue(w.addItems(s, toAdd));

            List<Item> shuffledItems = Arrays.asList(items);
            Collections.shuffle(shuffledItems);

            for (int i : indexes) {
                Set<Item> toRemove = new HashSet<>();
                toRemove.add(items[i]);
                Assert.assertTrue(w.removeItems(s, toRemove));
            }
        }
    }

    @Test
    public void testRemoveFromEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(2);
        Item i1 = w.createItem(LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            Assert.assertFalse(w.removeItems(s, items));
        }

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            Assert.assertTrue(w.addItems(s, items));
            Assert.assertTrue(w.removeItems(s, items));
            Assert.assertFalse(w.removeItems(s, items));

            items.clear();
            items.add(i1);
            Assert.assertFalse(w.removeItems(s, items));

            items.clear();
            items.add(i2);
            Assert.assertFalse(w.removeItems(s, items));
        }
    }

    @Test
    public void testRemoveItemNotInShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s = w.createShelf(1);
        Item i1 = w.createItem(LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            Assert.assertTrue(w.addItems(s, items));

            items.clear();
            items.add(i2);
            Assert.assertFalse(w.removeItems(s, items));
        }
    }
}
