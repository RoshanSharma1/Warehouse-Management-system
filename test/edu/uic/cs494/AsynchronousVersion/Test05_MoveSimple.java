package edu.uic.cs494.AsynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class Test05_MoveSimple {

    @Test
    public void moveItemToEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2).startThread();
        Shelf s2 = w.createShelf(2).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        Set<Item> toAdd = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertTrue(w.addItems(s1, toAdd));

        Set<Item> toMove = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertTrue(w.moveItems(s1, s2, toMove));
    }


    @Test
    public void moveItemToFullShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2).startThread();
        Shelf s2 = w.createShelf(2).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);
        Item i3 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i4 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

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
        Shelf s1 = w.createShelf(10).startThread();
        Shelf s2 = w.createShelf(10).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);
        Item i3 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i4 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        Set<Item> toAdd = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertTrue(w.addItems(s1, toAdd));

        Set<Item> toMove = new HashSet<>(Arrays.asList(new Item[]{i3, i4}));
        Assert.assertFalse(w.moveItems(s2, s1, toMove));
    }


    @Test
    public void testDeadlokcingMove() {
        Warehouse w = Warehouse.createWarehouse();

        Shelf s1 = w.createShelf(200).startThread();
        Shelf s2 = w.createShelf(200).startThread();

        Item[] items1 = new Item[50];
        Item[] items2 = new Item[50];

        for (int i = 0; i < items1.length; i++)
            items1[i] = w.createItem(Test01_AddItems.ITEM_NAME + "_1_" + i);

        for (int i = 0; i < items2.length; i++)
            items2[i] = w.createItem(Test01_AddItems.ITEM_NAME + "_2_" + i);

        w.addItems(s1, new HashSet(Arrays.asList(items1)));
        w.addItems(s2, new HashSet(Arrays.asList(items2)));

        int tsize = 4;
        Thread[] threads = new Thread[tsize * 2];

        for (int i = 0 ; i < tsize*2 ; i+=2) {
            threads[i] = new Thread(() -> {
                for (int j = 0 ; j < 10_000 ; j++) {
                    Optional<Item> itemToMove = w.getContents(s1).stream().findAny();
                    if (itemToMove.isPresent())
                        w.moveItems(s1, s2, Set.of(itemToMove.get()));
                }
            });
            threads[i+1] = new Thread(() -> {
                for (int j = 0 ; j < 10_000 ; j++) {
                    Optional<Item> itemToMove = w.getContents(s2).stream().findAny();
                    if (itemToMove.isPresent())
                        w.moveItems(s2, s1, Set.of(itemToMove.get()));
                }
            });
        }

        Test06_Multithreaded.runAllThreads(threads);

        Set<Item> expectedAllItems = new HashSet<>();
        expectedAllItems.addAll(Arrays.asList(items1));
        expectedAllItems.addAll(Arrays.asList(items2));

        // Everything that was there is still there
        Assert.assertEquals(expectedAllItems, w.getContents());

        for (Object i : w.getContents(s1)) {
            // Each object is in only one shelf
            Assert.assertTrue(expectedAllItems.remove(i));
        }

        for (Object i : w.getContents(s2)) {
            // Each object is in only one shelf
            Assert.assertTrue(expectedAllItems.remove(i));
        }

        // The contents of all shelfs are everything that was in the warehouse
        Assert.assertTrue(expectedAllItems.isEmpty());
    }


}
