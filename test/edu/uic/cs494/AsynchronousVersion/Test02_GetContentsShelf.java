package edu.uic.cs494.AsynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test02_GetContentsShelf {

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

            Assert.assertNotSame(toAdd, w.getContents(s));

            toAdd.clear();

            Set<Item> expected = new HashSet<>();
            for (int j = 0 ; j <= i ; j++)
                expected.add(items[j]);

            Assert.assertEquals(expected, w.getContents(s));

            w.getContents(s).clear();

            Assert.assertEquals(expected, w.getContents(s));
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
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            w.addItems(s, toAdd);
            toAdd.clear();
            toAdd.add(i2);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);

            w.getContents(s).clear();

            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
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
        Assert.assertEquals(expected, w.getContents(s));
        Assert.assertEquals(expected, s.items);
        w.getContents(s).clear();
        Assert.assertEquals(expected, s.items);
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
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            toAdd.add(i2);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i2);
            w.addItems(s, toAdd);
            w.addItems(s, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1,i2}));
            Assert.assertEquals(expected, w.getContents(s));
            Assert.assertEquals(expected, s.items);
        }
    }
}
