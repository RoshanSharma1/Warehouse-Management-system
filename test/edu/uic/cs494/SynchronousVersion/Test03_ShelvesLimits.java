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

public class Test03_ShelvesLimits {

    @Test
    public void testMaxCapacity() {
        Warehouse w = Warehouse.createWarehouse();
        int test = 20;
        Shelf[] shelves = new Shelf[10];

        for (int i = 0 ; i < shelves.length ; i++)
            shelves[i] = w.createShelf(i+1);

        for (int i = 0 ; i < test ; i++) {
            for (int j = 0 ; i < shelves.length ; i++) {
                Shelf s = shelves[j];
                Item item = w.createItem(Test01_SingleShelfLimits.ITEM_NAME + i);
                Set<Item> toAdd = new HashSet<>();
                toAdd.add(item);

                if (i <= j)
                    Assert.assertTrue(w.addItems(s, toAdd));
                else
                    Assert.assertFalse(w.addItems(s, toAdd));
            }
        }
    }

    @Test
    public void testNoRoomInDestination() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf from = w.createShelf(1);
        Shelf to = w.createShelf(1);

        Item i1 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);

        w.addItems(from, new HashSet(Arrays.asList(new Item[]{ i1 })));
        w.addItems(to, new HashSet(Arrays.asList(new Item[]{ i2 })));

        {
            HashSet<Item> expected = new HashSet(Arrays.asList(new Item[]{ i1 }));
            Assert.assertEquals(expected, w.getContents(from));
        }

        {
            HashSet<Item> expected = new HashSet(Arrays.asList(new Item[]{ i2 }));
            Assert.assertEquals(expected, w.getContents(to));
        }

        Assert.assertFalse(w.moveItems(from, to, new HashSet(Arrays.asList(new Item[]{ i1 }))));

        {
            HashSet<Item> expected = new HashSet(Arrays.asList(new Item[]{ i1 }));
            Assert.assertEquals(expected, w.getContents(from));
        }

        {
            HashSet<Item> expected = new HashSet(Arrays.asList(new Item[]{ i2 }));
            Assert.assertEquals(expected, w.getContents(to));
        }

    }

    @Test
    public void testItemAlreadyInWarehouse() {
        int size = 10;
        List<Integer> sequentialIndexes = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList1 = IntStream.range(0, size).boxed().collect(Collectors.toList());
        List<Integer> shuffledIndexesList2 = IntStream.range(0, size).boxed().collect(Collectors.toList());

        testItemAlreadyInWarehouse(sequentialIndexes, sequentialIndexes);
        testItemAlreadyInWarehouse(shuffledIndexesList1, shuffledIndexesList2);
    }

    private void testItemAlreadyInWarehouse(List<Integer> itemIndexes, List<Integer> shelfIndexes) {
        int size = itemIndexes.size();
        Warehouse w = Warehouse.createWarehouse();
        Shelf[] shelves = new Shelf[size];

        for (int i = 0 ; i < size ; i++)
            shelves[i] = w.createShelf(size);

        Item[] items = new Item[size];
        for (int i = 0 ; i < size ; i++) {
            items[i] = w.createItem(Test01_SingleShelfLimits.ITEM_NAME+i);
        }

        for (int i : itemIndexes) {
            Shelf s  = shelves[shelfIndexes.get(i)];
            Shelf ss = shelves[shelfIndexes.get((i+1)%size)];

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

                Assert.assertFalse(w.addItems(ss, toAdd));
                Assert.assertFalse(w.addItems(ss, new HashSet<>(Arrays.asList(new Item[]{items[i]}))));
            }

            {
                Set<Item> toAdd = new HashSet<>();
                toAdd.add(items[i]);
                Item anotherItem = w.createItem(Test01_SingleShelfLimits.ANOTHER_ITEM_NAME + i);
                toAdd.add(anotherItem);
                Assert.assertFalse(w.addItems(s, toAdd));
                Assert.assertFalse(w.addItems(s, new HashSet<>(Arrays.asList(new Item[]{items[i], anotherItem}))));

                Assert.assertFalse(w.addItems(ss, toAdd));
                Assert.assertFalse(w.addItems(ss, new HashSet<>(Arrays.asList(new Item[]{items[i]}))));
            }
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
            shelves[i] = w.createShelf(size);

        Item[] items = new Item[size];
        for (int i = 0 ; i < size ; i++)
            items[i] = w.createItem(Test01_SingleShelfLimits.ITEM_NAME + i);

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
        Shelf s1 = w.createShelf(2);
        Shelf s2 = w.createShelf(2);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

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

    @Test
    public void moveItemToEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2);
        Shelf s2 = w.createShelf(2);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);
        Item i3 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i4 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        Set<Item> toAdd = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertTrue(w.addItems(s1, toAdd));

        Set<Item> toMove = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertFalse(w.moveItems(s2, s1, toMove));
        Assert.assertTrue(w.moveItems(s1, s2, toMove));
        Assert.assertFalse(w.removeItems(s1, toMove));

        Set<Item> toAddMore = new HashSet<>(Arrays.asList(new Item[]{i3, i4}));
        Assert.assertTrue(w.addItems(s1, toAddMore));

        Assert.assertTrue(w.removeItems(s2, toMove));
    }

    @Test
    public void moveItemToFullShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2);
        Shelf s2 = w.createShelf(2);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);
        Item i3 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i4 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        Set<Item> toAdd = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertTrue(w.addItems(s1, toAdd));

        Set<Item> toAddMore = new HashSet<>(Arrays.asList(new Item[]{i3, i4}));
        Assert.assertTrue(w.addItems(s2, toAddMore));

        Set<Item> toMove = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertFalse(w.moveItems(s1, s2, toMove));
    }

    @Test
    public void moveItemFromEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(10);
        Shelf s2 = w.createShelf(10);
        Item i1 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);
        Item i3 = w.createItem(Test01_SingleShelfLimits.LAPTOP_ITEM_NAME);
        Item i4 = w.createItem(Test01_SingleShelfLimits.DESKTOP_ITEM_NAME);

        Set<Item> toAdd = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertTrue(w.addItems(s1, toAdd));

        Set<Item> toMove = new HashSet<>(Arrays.asList(new Item[]{i3, i4}));
        Assert.assertFalse(w.moveItems(s2, s1, toMove));
    }
}
