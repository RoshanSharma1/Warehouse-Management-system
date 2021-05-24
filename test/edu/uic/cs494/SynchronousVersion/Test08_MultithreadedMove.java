package edu.uic.cs494.SynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

public class Test08_MultithreadedMove {
    @Test
    public void testMove() {
        int n = 5;
        Warehouse w = Warehouse.createWarehouse();

        Thread[] threads = new Thread[8];

        Shelf[] shelves = new Shelf[10];
        int size = 2 * n;

        Item[] items = new Item[shelves.length * n];

        for (int i = 0 ; i < shelves.length ; i++) {
            shelves[i] = w.createShelf(size);
            for (int j = 0 ; j < n ; j++) {
                int itemNo = i * n + j;
                Item item = w.createItem(Test01_SingleShelfLimits.ITEM_NAME + "-" + itemNo);
                items[itemNo] = item;
                w.addItems(shelves[i], new HashSet(Arrays.asList(new Item[]{item})));
            }
        }

//        for (int t = 0 ; t < threads.length ; t++) {
//            threads[t] = new Thread(() -> {
//                for (int i = 0 ; i < shelves.length ; i++) {
//                    for (int j = 0 ; j < n ; j++) {
//                        for (int k = 0 ; k < shelves.length ; k++) {
//                            int itemNo = i * n + j;
//                            w.moveItems(shelves[i], shelves[k], new HashSet(Arrays.asList(new Item[]{ items[itemNo] })));
//                        }
//                    }
//                }
//            });
//        }

        for (int i = 0 ; i < threads.length ; i++) {
            threads[i] = new Thread(() -> {
                Random r = new Random();
                for (int j = 0 ; j < 10_000 ; j++) {
                    int from = r.nextInt(shelves.length);
                    int to   = r.nextInt(shelves.length);
                    Optional<Item> itemToMove = w.getContents(shelves[from]).stream().findAny();
                    if (itemToMove.isPresent())
                        w.moveItems(shelves[from], shelves[to], new HashSet(Arrays.asList(new Item[]{ itemToMove.get() })));
                }
            });
        }


        HashSet<Item> expectedAllItems = new HashSet<>(Arrays.asList(items));
        Assert.assertEquals(expectedAllItems, w.getContents());

        Test06_MultithreadedAdd.runAllThreads(threads);

        // Everything that was there is still there
        Assert.assertEquals(expectedAllItems, w.getContents());

        for (Shelf s : shelves) {
            for (Object i : w.getContents(s)) {
                // Each object is in only one shelf
                Assert.assertTrue(expectedAllItems.remove(i));
            }
        }

        // The contents of all shelfs are everything that was in the warehouse
        Assert.assertTrue(expectedAllItems.isEmpty());
    }
}
