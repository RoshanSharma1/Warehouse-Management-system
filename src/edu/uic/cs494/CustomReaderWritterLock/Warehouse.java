package edu.uic.cs494.CustomReaderWritterLock;

import edu.uic.cs494.CustomReaderWritterLock.solution.WarehouseSolution;

import java.util.Set;

public interface Warehouse <S extends Shelf, I extends Item> {

    static Warehouse createWarehouse() {
        return new WarehouseSolution();
    }

    S createShelf(int size);

    I createItem(String description);

    boolean addItems(S s, Set<I> items);

    boolean removeItems(S s, Set<I> items);

    boolean moveItems(S from, S to, Set<I> items);

    Set<I> getContents();

    Set<I> getContents(S s);

}
