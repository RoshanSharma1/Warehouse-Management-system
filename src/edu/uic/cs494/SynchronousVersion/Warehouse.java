package edu.uic.cs494.SynchronousVersion;

import edu.uic.cs494.SynchronousVersion.solution.WarehouseSolution;

import java.util.List;
import java.util.Set;

public interface Warehouse <S extends Shelf, I extends Item> {

    public static Warehouse createWarehouse() {
       return new WarehouseSolution();
    }

    public S createShelf(int size);

    public I createItem(String description);

    public boolean addItems(S s, Set<I> items);

    public boolean removeItems(S s, Set<I> items);

    public boolean moveItems(S from, S to, Set<I> items);

    public Set<I> getContents();

    public Set<I> getContents(S s);

    public List<Action<S>> audit(I i);

    public List<Action<I>> audit(S s);
}
