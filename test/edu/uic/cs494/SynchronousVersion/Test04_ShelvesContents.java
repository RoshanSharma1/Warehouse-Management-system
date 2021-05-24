package edu.uic.cs494.SynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Test04_ShelvesContents {

    @Test
    public void testCapacityAdd() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(1);
        Shelf s2 = w.createShelf(10);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            toAdd.add(i2);
            w.addItems(s1, toAdd);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(expected, w.getContents(s2));
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            w.addItems(s1, toAdd);
            toAdd.clear();
            toAdd.add(i2);
            w.addItems(s1, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(new HashSet<>(), w.getContents(s2));
        }
    }

    @Test
    public void testCapacitySameDescription() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        testCapacitySameDescription(sequentialIndexes, sequentialIndexes);
        testCapacitySameDescription(shuffledIndexesList1, shuffledIndexesList2);
    }

    public void testCapacitySameDescription(List<Integer> itemIndexes, List<Integer> shelfIndexes) {
        int size = itemIndexes.size();
        Warehouse w = Warehouse.createWarehouse();
        Shelf[] shelves = new Shelf[shelfIndexes.size()];

        for (int i = 0 ; i< shelves.length ; i++)
            shelves[i] = w.createShelf(size);

        Item[] items = new Item[size];
        Set<Item>[] expected = new Set[size];
        Set<Item> expectedAll = new HashSet<>();

        for (int i : itemIndexes) {
            int s = shelfIndexes.get(i);
            items[i] = w.createItem(Test01_SingleShelfLimits.ITEM_NAME);
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(items[i]);
            w.addItems(shelves[s], toAdd);
            expected[s] = new HashSet<>();
            expected[s].add(items[i]);
            expectedAll.add(items[i]);
        }


        Assert.assertEquals(size, w.getContents().size());
        Assert.assertEquals(expectedAll, w.getContents());

        for (int i : shelfIndexes)
            Assert.assertEquals(expected[i], w.getContents(shelves[i]));
    }

    @Test
    public void testCapacityDuplicatedItem() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(10);
        Shelf s2 = w.createShelf(10);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i1);
            w.addItems(s1, toAdd);
            w.addItems(s2, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(new HashSet<>(), w.getContents(s2));
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i2);
            w.addItems(s2, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents(s1));

            expected = new HashSet<>(Arrays.asList(new Item[]{i2}));
            Assert.assertEquals(expected, w.getContents(s2));

            expected = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
            Assert.assertEquals(expected, w.getContents());
        }

        {
            Set<Item> toAdd = new HashSet<>();
            toAdd.add(i2);
            w.addItems(s1, toAdd);
            w.addItems(s2, toAdd);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents(s1));

            expected = new HashSet<>(Arrays.asList(new Item[]{i2}));
            Assert.assertEquals(expected, w.getContents(s2));

            expected = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
            Assert.assertEquals(expected, w.getContents());
        }
    }

    @Test
    public void testRemoveFromShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2);
        Shelf s2 = w.createShelf(2);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);
            w.addItems(s1, items);
            w.removeItems(s2, items);
            w.removeItems(s1, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(expected, w.getContents(s2));
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
            Assert.assertEquals(expected, w.getContents(s2));
            Assert.assertEquals(new HashSet<>(), w.getContents(s1));

            items.clear();
            items.add(i2);
            w.removeItems(s2, items);

            expected.clear();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(expected, w.getContents(s2));
        }
    }

    @Test
    public void testRemoveFromEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2);
        Shelf s2 = w.createShelf(2);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            items.add(i2);

            w.removeItems(s1, items);
            w.removeItems(s2, items);

            Set<Item> expected = new HashSet<>();
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(expected, w.getContents(s2));
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
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(expected, w.getContents(s2));

            items.clear();
            items.add(i1);
            w.removeItems(s1, items);
            w.removeItems(s2, items);
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(expected, w.getContents(s2));

            items.clear();
            items.add(i2);
            w.removeItems(s1, items);
            w.removeItems(s2, items);
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(expected, w.getContents(s2));
        }
    }

    @Test
    public void testRemoveItemNotInShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(1);
        Shelf s2 = w.createShelf(1);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        {
            Set<Item> items = new HashSet<>();
            items.add(i1);
            w.addItems(s1, items);

            items.clear();
            items.add(i2);
            w.removeItems(s2, items);

            Set<Item> expected = new HashSet<>(Arrays.asList(new Item[]{i1}));
            Assert.assertEquals(expected, w.getContents());
            Assert.assertEquals(expected, w.getContents(s1));
            Assert.assertEquals(new HashSet<>(), w.getContents(s2));

        }
    }
}
