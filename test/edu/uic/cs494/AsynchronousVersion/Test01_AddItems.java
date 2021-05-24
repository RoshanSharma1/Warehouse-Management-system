package edu.uic.cs494.AsynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test01_AddItems {
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
        for (int i = 0; i < size; i++) {
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
        Shelf s = w.createShelf(size).startThread();

        for (int i = 0; i < test; i++) {
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
        Shelf s = w.createShelf(size).startThread();

        Item[] items = new Item[size];
        for (int i = 0; i < size; i++) {
            items[i] = w.createItem(ITEM_NAME + i);
        }

        for (int i : indexes) {
            if (i > 0) {
                Set<Item> toAdd = new HashSet<>();
                toAdd.add(items[i]);
                toAdd.add(items[i - 1]);
                Assert.assertFalse(w.addItems(s, toAdd));
                Assert.assertFalse(w.addItems(s, new HashSet<>(Arrays.asList(new Item[]{items[i], items[i - 1]}))));
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

}
