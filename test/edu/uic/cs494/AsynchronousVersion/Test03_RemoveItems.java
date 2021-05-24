package edu.uic.cs494.AsynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test03_RemoveItems {

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
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);

            items.clear();
            items.add(i2);
            w.removeItems(s, items);

            expected.clear();
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
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
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
        }

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            w.addItems(s, items);
            w.removeItems(s, items);
            w.removeItems(s, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);

            items.clear();
            items.add(i1);
            w.removeItems(s, items);
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);

            items.clear();
            items.add(i2);
            w.removeItems(s, items);
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
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
            Assert.assertEquals(expected, w.getContents(s));

            w.getContents(s).clear();

            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
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
        Shelf s = w.createShelf(size).startThread();

        Item[] items = new Item[size];
        for (int i = 0 ; i < size ; i++) {
            items[i] = w.createItem(Test01_AddItems.ITEM_NAME + i);
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
        Shelf s = w.createShelf(2).startThread();

        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

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
        Shelf s = w.createShelf(1).startThread();

        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            Assert.assertTrue(w.addItems(s, items));

            items.clear();
            items.add(i2);
            Assert.assertFalse(w.removeItems(s, items));
        }
    }

    @Test
    public void testRemoveFromWarehouse() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        testRemoveFromShelf(sequentialIndexes, sequentialIndexes);
        testRemoveFromShelf(shuffledIndexesList1, shuffledIndexesList2);
    }

    public void testRemoveFromShelf(List<Integer> itemIndexes, List<Integer> shelfIndexes) {

        Warehouse w = Warehouse.createWarehouse();
        int size = itemIndexes.size();
        Shelf[] shelves = new Shelf[shelfIndexes.size()];

        for (int i = 0 ; i < shelves.length ; i++)
            shelves[i] = w.createShelf(size).startThread();

        Item[] items = new Item[size];
        for (int i = 0 ; i < size ; i++)
            items[i] = w.createItem(Test01_AddItems.ITEM_NAME + i);

        for (int i : itemIndexes) {
            Set<Item> toAdd = new HashSet<>();
            Shelf s = shelves[shelfIndexes.get(i)];
            Shelf ss = shelves[shelfIndexes.get((i+1)%shelfIndexes.size())];
            toAdd.add(items[i]);
            Assert.assertFalse(w.removeItems(s, toAdd));
            Assert.assertFalse(w.removeItems(ss, toAdd));

            Assert.assertTrue(w.addItems(s, toAdd));

            Assert.assertFalse(w.removeItems(ss, toAdd));
            Assert.assertTrue(w.removeItems(s, toAdd));
        }
    }

    @Test
    public void testRemoveFromEmptyWarehouse() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2).startThread();
        Shelf s2 = w.createShelf(2).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            Assert.assertFalse(w.removeItems(s1, items));
            Assert.assertFalse(w.removeItems(s2, items));
        }

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            Assert.assertTrue(w.addItems(s1, items));
            Assert.assertTrue(w.removeItems(s1, items));
            Assert.assertFalse(w.removeItems(s1, items));
            Assert.assertFalse(w.removeItems(s2, items));

            items.clear();
            items.add(i1);
            Assert.assertFalse(w.removeItems(s1, items));
            Assert.assertFalse(w.removeItems(s2, items));

            items.clear();
            items.add(i2);
            Assert.assertFalse(w.removeItems(s1, items));
            Assert.assertFalse(w.removeItems(s2, items));
        }
    }
}
