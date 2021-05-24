package edu.uic.cs494.CustomReaderWritterLock.solution;

import edu.uic.cs494.CustomReaderWritterLock.Action;
import edu.uic.cs494.CustomReaderWritterLock.Shelf;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class ShelfSolution implements Shelf {
    Set<ItemSolution> itemSolutions;
    int size;
    private final long id;
    public List<Action<ItemSolution>> auditLogs;

    //thread synchronization
    private final ReadWriteLock readWriteLock = new SolutionReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public ShelfSolution(Set<ItemSolution> itemSolutions, int size, long id) {
        this.itemSolutions = itemSolutions;
        this.size = size;
        this.id = id;
        this.auditLogs = new LinkedList<>();
    }

    public void getReadLock(){
        readLock.lock();
    }
    public void getWriteLock(){
        writeLock.lock();
    }
    public void unlockRead(){
        readLock.unlock();
    }
    public void unlockWrite(){
        writeLock.unlock();
    }
    public int compareTo(ShelfSolution ba) {
        return (this.id > ba.id) ? 1 : (this.id < ba.id) ? -1 : 0;
    }

    public  boolean itemExists (ItemSolution item){
        return this.itemSolutions.contains(item);
    }
    public boolean itemsExist (Set<ItemSolution> items){
        return this.itemSolutions.containsAll(items);
    }
    public int totalItemCount(){
        return this.itemSolutions.size();
    }
}
