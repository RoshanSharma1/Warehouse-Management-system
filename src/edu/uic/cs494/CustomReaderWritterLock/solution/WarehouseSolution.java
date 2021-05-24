package edu.uic.cs494.CustomReaderWritterLock.solution;

import edu.uic.cs494.CustomReaderWritterLock.Warehouse;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class WarehouseSolution implements Warehouse<ShelfSolution,ItemSolution> {
    LinkedHashSet<ShelfSolution> shelfSolutions;
    private static final AtomicLong nextID = new AtomicLong(0);
    public WarehouseSolution() {
        shelfSolutions = new LinkedHashSet<>() ;
    }

    @Override
     public ShelfSolution createShelf(int size) {
        ShelfSolution shelfSolution = new ShelfSolution(new HashSet<>(), size, nextID.getAndIncrement());
        this.shelfSolutions.add(shelfSolution);
        return shelfSolution;
    }

    @Override
     public ItemSolution createItem(String description) {
       return new ItemSolution(description);
    }

    @Override
     public boolean addItems(ShelfSolution shelfSolution, Set<ItemSolution> items) {
        try {
            Set<ItemSolution> itemSolutions = new HashSet<>();
            for (ShelfSolution selfI : this.shelfSolutions) {
                if(!selfI.equals(shelfSolution)){
                    selfI.getReadLock();
                }
                else{
                    selfI.getWriteLock();
                }
            }
            for (ShelfSolution selfI : this.shelfSolutions) {
                 itemSolutions.addAll(selfI.itemSolutions);
            }
            if (shelfSolution.size - shelfSolution.itemSolutions.size() >= items.size()) {
                for (ItemSolution item : items) {
                        if (itemSolutions.contains(item)) {
                            return false;
                        }
                }
                shelfSolution.itemSolutions.addAll(items);
                return true;
            }
            return false;
        }
        finally {
            for (ShelfSolution selfI : this.shelfSolutions) {
                if(!selfI.equals(shelfSolution)){
                    selfI.unlockRead();
                }
                else{
                    selfI.unlockWrite();
                }
            }
        }
    }

    @Override
     public boolean removeItems(ShelfSolution shelfSolution, Set<ItemSolution> items) {
        shelfSolution.getWriteLock();
        try {
            if (!shelfSolution.itemSolutions.containsAll(items)) {
                return false;
            }
            shelfSolution.itemSolutions.removeAll(items);
            return true;
        }
        finally {
            shelfSolution.unlockWrite();
        }
    }

    @Override
     public boolean moveItems(ShelfSolution from, ShelfSolution to, Set<ItemSolution> items) {
        if(from == to) return false;
        ShelfSolution former, latter;
        if (from.compareTo(to) < 0) {
            former = from;
            latter = to;
        } else {
            former = to;
            latter = from;
        }
        former.getWriteLock();
        latter.getWriteLock();
        try {
            if (to.size - to.totalItemCount() >= items.size()) {
                if (from.itemsExist(items)) {
                    for (ItemSolution item : items) {
                        if (to.itemExists(item)) return false;
                    }
                    to.itemSolutions.addAll(items);
                    from.itemSolutions.removeAll(items);

                    return true;
                }
            }
            return false;
        }
        finally {
          latter.unlockWrite();
          former.unlockWrite();
        }
    }

    @Override
     public Set<ItemSolution> getContents() {
        try {
            for (ShelfSolution selfI : this.shelfSolutions) {
                selfI.getReadLock();
            }
            Set<ItemSolution> itemSolutions = new HashSet<>();
            this.shelfSolutions.forEach(shelfSolution1 -> {
                itemSolutions.addAll(shelfSolution1.itemSolutions);
            });

            return itemSolutions;
        }
        finally {
            for(ShelfSolution selfI: this.shelfSolutions){
                selfI.unlockRead();
            }
        }

    }

    @Override
     public Set<ItemSolution> getContents(ShelfSolution shelfSolution) {
        Set<ItemSolution> itemSolutions =  new HashSet<>();
        shelfSolution.getReadLock();
        try {
            itemSolutions.addAll(shelfSolution.itemSolutions);
            return itemSolutions;
        }
        finally {
            shelfSolution.unlockRead();
        }
    }

}
