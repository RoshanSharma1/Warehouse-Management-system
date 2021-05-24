package edu.uic.cs494.CustomReaderWritterLock;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class Test07_MultithreadedRemove {
    // Each thread (out of 10 threads) removes 100 items from warehouse with 1000 items

    @Test
    public void testRemove() {
        Warehouse w = Warehouse.createWarehouse();

        Shelf[] shelves = new Shelf[10];
        int size = 100;

        for (int i = 0 ; i < shelves.length ; i++) {
            shelves[i] = w.createShelf(size);
            Set<Item> items = new HashSet<>();
            for (int j = 0 ; j < size ; j++) {
                Item item = w.createItem(Test01_SingleShelfLimits.ITEM_NAME + " #" + (i*size + j));
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

        Test06_MultithreadedAdd.runAllThreads(threads);

        Assert.assertEquals(0, w.getContents().size());
        Assert.assertEquals(new HashSet(), w.getContents());

        for (int i = 0 ; i < shelves.length ; i++)
            Assert.assertEquals(new HashSet<>(), w.getContents(shelves[i]));

    }
}
