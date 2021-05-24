package edu.uic.cs494.AsynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class Test06_Multithreaded {
    // Each thread adds 100 items, final warehouse should have 1000 items

    @Test
    public void testAdd() {
        Warehouse w = Warehouse.createWarehouse();

        Shelf[] shelves = new Shelf[10];
        int size = 100;

        for (int i = 0 ; i < shelves.length ; i++)
            shelves[i] = w.createShelf(size).startThread();

        Thread[] threads = new Thread[10];
        Set<Item>[] allItems = new Set[threads.length];

        for (int i = 0 ; i < threads.length ; i++) {
            int threadID = i;
            allItems[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                for (int j = 0 ; j < size ; j++) {
                    Item item = w.createItem(Test01_AddItems.ITEM_NAME + " Thread " + threadID + " #" + j);
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

        {
            Set<Item> itemsOnShelves = new HashSet<>();
            for (int i = 0; i < shelves.length; i++)
                itemsOnShelves.addAll(w.getContents(shelves[i]));

            Assert.assertEquals(expectedAllItems, itemsOnShelves);
        }
        {
            Set<Item> itemsOnShelves = new HashSet<>();
            for (int i = 0; i < shelves.length; i++)
                itemsOnShelves.addAll(shelves[i].items);

            Assert.assertEquals(expectedAllItems, itemsOnShelves);
        }
    }

    /*default*/ static void runAllThreads(Thread[] threads) {
        // Throw uncaught exceptions on this thread to fail tests
        for (int i = 0 ; i < threads.length ; i++)
            threads[i].setUncaughtExceptionHandler((t,ex) -> { System.err.println(ex.getMessage()); ex.printStackTrace(); throw new Error(ex); });

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

    @Test
    public void testRemove() {
        Warehouse w = Warehouse.createWarehouse();

        Shelf[] shelves = new Shelf[10];
        int size = 100;

        for (int i = 0 ; i < shelves.length ; i++) {
            shelves[i] = w.createShelf(size).startThread();
            Set<Item> items = new HashSet<>();
            for (int j = 0 ; j < size ; j++) {
                Item item = w.createItem(Test01_AddItems.ITEM_NAME + " #" + (i*size + j));
                items.add(item);
            }
            w.addItems(shelves[i], items);
        }

        Thread[] threads = new Thread[10];
        Set<Item>[] allItems = new Set[threads.length];

        for (int i = 0 ; i < threads.length ; i++) {
            int threadID = i;
            allItems[i] = new HashSet<>();
            threads[i] = new Thread(() -> {
                Random rnd = new Random();
                int removed = 0;
                while (removed < size) {
                    Shelf s = shelves[rnd.nextInt(shelves.length)];
                    Optional<Item> item = w.getContents(s).stream().findAny();
                    if (!item.isPresent())
                        continue;
                    if (w.removeItems(s, new HashSet<Item>(Arrays.asList(new Item[]{ item.get() }))))
                        removed++;
                }
            });
        }

        Test06_Multithreaded.runAllThreads(threads);

        Assert.assertEquals(0, w.getContents().size());
        Assert.assertEquals(new HashSet(), w.getContents());

        for (int i = 0 ; i < shelves.length ; i++)
            Assert.assertEquals(new HashSet<>(), w.getContents(shelves[i]));

        for (int i = 0 ; i < shelves.length ; i++)
            Assert.assertEquals(new HashSet<>(), shelves[i].items);

    }
}
