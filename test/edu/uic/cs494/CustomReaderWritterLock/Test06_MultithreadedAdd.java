package edu.uic.cs494.CustomReaderWritterLock;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Test06_MultithreadedAdd {

    // Each thread adds 100 items, final warehouse should have 1000 items

    @Test
    public void testAdd() {
        Warehouse w = Warehouse.createWarehouse();

        Shelf[] shelves = new Shelf[10];
        int size = 100;

        for (int i = 0 ; i < shelves.length ; i++)
            shelves[i] = w.createShelf(size);

        Thread[] threads = new Thread[10];
        Set<Item>[] allItems = new Set[threads.length];

        for (int i = 0 ; i < threads.length ; i++) {
            int threadID = i;
            allItems[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                for (int j = 0 ; j < size ; j++) {
                    Item item = w.createItem(Test01_SingleShelfLimits.ITEM_NAME + " Thread " + threadID + " #" + j);
                    Set<Item> items = new HashSet<>(Arrays.asList(new Item[]{ item }));
                    allItems[threadID].add(item);
                    while (true) {
                        Shelf s = shelves[rnd.nextInt(shelves.length)];
                        if (w.getContents(s).size() == size || !w.addItems(s, items))
                            continue;
                        break;
                    }
                }
            });
        }

        runAllThreads(threads);

        Assert.assertEquals(size*shelves.length, w.getContents().size());

        Set<Item> expectedAllItems = new HashSet<>();
        for (int i = 0 ; i < threads.length ; i++)
            expectedAllItems.addAll(allItems[i]);

        Assert.assertEquals(expectedAllItems, w.getContents());

        Set<Item> itemsOnShelves = new HashSet<>();
        for (int i = 0 ; i < shelves.length ; i++)
            itemsOnShelves.addAll(w.getContents(shelves[i]));

        Assert.assertEquals(expectedAllItems, itemsOnShelves);
    }

    /*default*/ static void runAllThreads(Thread[] threads) {
        // Start all threads
        for (int i = 0 ; i < threads.length ; i++)
            threads[i].start();

        // Wait for all threads to finish
        for (int i = 0 ; i < threads.length ; i++) {
            while (threads[i].isAlive()) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }
}
