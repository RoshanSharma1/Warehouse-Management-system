package edu.uic.cs494.AsynchronousVersion;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class Test08_MoveComplex {

    @Test
    public void moveItemToEmptyShelf() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf s1 = w.createShelf(2).startThread();
        Shelf s2 = w.createShelf(2).startThread();
        Item i1 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);
        Item i3 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);
        Item i4 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);

        Set<Item> toAdd = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertTrue(w.addItems(s1, toAdd));

        Set<Item> toMove = new HashSet<>(Arrays.asList(new Item[]{i1, i2}));
        Assert.assertFalse((boolean)w.moveItemsAsync(s2, s1, toMove).getResult());
        Assert.assertTrue((boolean)w.moveItemsAsync(s1, s2, toMove).getResult());
        Assert.assertFalse((boolean)w.removeItemsAsync(s1, toMove).getResult());

        Set<Item> toAddMore = new HashSet<>(Arrays.asList(new Item[]{i3, i4}));
        Assert.assertTrue(w.addItems(s1, toAddMore));

        Assert.assertTrue(w.removeItems(s2, toMove));
    }


    @Test
    public void testNoRoomInDestination() {
        Warehouse w = Warehouse.createWarehouse();
        Shelf from = w.createShelf(1).startThread();
        Shelf to = w.createShelf(1).startThread();

        Item i1 = w.createItem(Test01_AddItems.DESKTOP_ITEM_NAME);
        Item i2 = w.createItem(Test01_AddItems.LAPTOP_ITEM_NAME);

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
    public void testRemoveMove() {
        Warehouse w = Warehouse.createWarehouse();

        int size = 100;
        Shelf[] shelves = new Shelf[1000];

        for (int i = 0; i < shelves.length; i++)
            shelves[i] = w.createShelf(size).startThread();

        int tsize = 4;
        int work = 500;
        Thread[] threads = new Thread[(tsize * 2) + 1];

        {
            Random r = new Random();
            for (int i = 0; i < tsize * work; i++) {
                Shelf to = shelves[r.nextInt(shelves.length)];
                w.addItems(to, Set.of(w.createItem(Test01_AddItems.ITEM_NAME)));
            }
        }

        for (int i = 0 ; i < tsize*2 ; i+=2) {
            threads[i] = new Thread(() -> {
                Random r = new Random();
                for (int j = 0 ; j < work ; j++) {
                    Shelf from = shelves[r.nextInt(shelves.length)];
                    Shelf to = shelves[r.nextInt(shelves.length)];
                    Optional<Item> itemToMove = w.getContents(from).stream().findAny();
                    if (itemToMove.isPresent())
                        w.moveItems(from, to, Set.of(itemToMove.get()));
                }
            });
            threads[i+1] = new Thread(() -> {
                Random r = new Random();
                for (int j = 0 ; j < work ; j++) {
                    while (true) {
                        Shelf from = shelves[r.nextInt(shelves.length)];
                        Optional<Item> itemToRemove = w.getContents(from).stream().findAny();
                        if (itemToRemove.isPresent() && w.removeItems(from, Set.of(itemToRemove.get())))
                            break;
                    }
                }
            });
        }

        LinkedList<Set<Item>> snapshots = new LinkedList<>();
        threads[threads.length-1] = new Thread(() -> {
            for (int j = 0 ; j < work ; j++) {
                snapshots.addLast(w.getContents());
            }
        });

        Test06_Multithreaded.runAllThreads(threads);

        Set<Item> expectedAllItems = new HashSet<>(snapshots.getLast());

        // Everything that warehouse is empty
        Assert.assertEquals(new HashSet(), w.getContents());

        for (Shelf s : shelves) {
            Assert.assertTrue(w.getContents(s).isEmpty());
        }

//        // Snapshots only decrease
//        Set<Item> prev = snapshots.getFirst();
//        for (Set<Item> snap : snapshots) {
//            Assert.assertTrue(snap.size() <= prev.size());
//            Assert.assertTrue(prev.containsAll(snap));
//            prev = snap;
//        }
    }

    @Test
    public void testAddMove() {
        Warehouse w = Warehouse.createWarehouse();

        int work = 100;

        int size = 100;
        Shelf[] shelves = new Shelf[1000];

        for (int i = 0; i < shelves.length; i++) {
            shelves[i] = w.createShelf(size).startThread();
            w.addItems(shelves[i], Set.of(w.createItem(Test01_AddItems.ITEM_NAME)));
        }

        int tsize = 4;
        Thread[] threads = new Thread[(tsize * 2) + 1];

        for (int i = 0 ; i < tsize*2 ; i+=2) {
            threads[i] = new Thread(() -> {
                Random r = new Random();
                for (int j = 0 ; j < work ; j++) {
                    Shelf from = shelves[r.nextInt(shelves.length)];
                    Shelf to = shelves[r.nextInt(shelves.length)];
                    Optional<Item> itemToMove = w.getContents(from).stream().findAny();
                    if (itemToMove.isPresent())
                        w.moveItems(from, to, Set.of(itemToMove.get()));
                }
            });
            threads[i+1] = new Thread(() -> {
                Random r = new Random();
                for (int j = 0 ; j < work ; j++) {
                    Item itemToAdd = w.createItem(Test01_AddItems.ITEM_NAME);
                    while (true) {
                        Shelf to = shelves[r.nextInt(shelves.length)];
                        if (w.addItems(to, Set.of(itemToAdd)))
                            break;
                    }
                }
            });
        }

        LinkedList<Set<Item>> snapshots = new LinkedList<>();
        threads[threads.length-1] = new Thread(() -> {
            for (int j = 0 ; j < work ; j++) {
                snapshots.addLast(w.getContents());
            }
        });

        Test06_Multithreaded.runAllThreads(threads);

        Set<Item> allItems = w.getContents();

        // Everything that was there is still there
        Assert.assertTrue(allItems.containsAll(snapshots.getLast()));

        for (Shelf s : shelves) {
            for (Object i : w.getContents(s)) {
                // Each object is in only one shelf
                Assert.assertTrue(allItems.remove(i));
            }
        }

        // The contents of all shelves are everything that was in the warehouse
        Assert.assertTrue(allItems.isEmpty());

//        // Snapshots only increase
//        Set<Item> prev = snapshots.getFirst();
//        for (Set<Item> snap : snapshots) {
//            Assert.assertTrue(snap.size() >= prev.size());
//            Assert.assertTrue(snap.containsAll(prev));
//            prev = snap;
//        }
    }

    @Test
    public void testMove() {
        int n = 5;
        Warehouse w = Warehouse.createWarehouse();

        Thread[] threads = new Thread[4];

        Shelf[] shelves = new Shelf[10];
        int size = 2 * n;

        Item[] items = new Item[shelves.length * n];

        for (int i = 0 ; i < shelves.length ; i++) {
            shelves[i] = w.createShelf(size).startThread();
            for (int j = 0 ; j < n ; j++) {
                int itemNo = i * n + j;
                Item item = w.createItem(Test01_AddItems.ITEM_NAME + "-" + itemNo);
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
                for (int j = 0 ; j < 1000 ; j++) {
                    int from = r.nextInt(shelves.length);
                    int to   = r.nextInt(shelves.length);
                    Optional<Item> itemToMove = w.getContents(shelves[from]).stream().findAny();
                    if (itemToMove.isPresent())
                        w.moveItems(shelves[from], shelves[to], new HashSet(Arrays.asList(new Item[]{ itemToMove.get() })));
                }
            });
        }


        HashSet<Item> expectedAllItems  = new HashSet<>(Arrays.asList(items));
        HashSet<Item> expectedAllItems2 = new HashSet<>(Arrays.asList(items));
        Assert.assertEquals(expectedAllItems, w.getContents());

        Test06_Multithreaded.runAllThreads(threads);

        // Everything that was there is still there
        Assert.assertEquals(expectedAllItems, w.getContents());

        for (Shelf s : shelves) {
            for (Object i : w.getContents(s)) {
                // Each object is in only one shelf
                Assert.assertTrue(expectedAllItems.remove(i));
            }
            for (Object i : s.items) {
                // Each object is in only one shelf
                Assert.assertTrue(expectedAllItems2.remove(i));
            }
        }

        // The contents of all shelfs are everything that was in the warehouse
        Assert.assertTrue(expectedAllItems.isEmpty());
        Assert.assertTrue(expectedAllItems2.isEmpty());
    }
}
