package edu.uic.cs494.AsynchronousVersion.solution;
import edu.uic.cs494.AsynchronousVersion.Action;
import edu.uic.cs494.AsynchronousVersion.Warehouse;
import edu.uic.cs494.AsynchronousVersion.Result;
import edu.uic.cs494.AsynchronousVersion.Item;
import java.util.HashSet;
import java.util.Set;

public class SolutionWarehouse implements Warehouse<SolutionShelf, SolutionItem> {
    Set<SolutionShelf> solutionShelves;

    public SolutionWarehouse() {
        solutionShelves = new HashSet<>();
    }

    @Override
    public SolutionShelf createShelf(int size) {
        SolutionShelf self = new SolutionShelf(size);
        solutionShelves.add(self);
        return self;
    }

    @Override
    public SolutionItem createItem(String description) {
        return new SolutionItem(description);
    }

    @Override
    public boolean addItems(SolutionShelf solutionShelf, Set<SolutionItem> items) {
        SolutionResult<Boolean> result = new SolutionResult<Boolean>();
        Action a = new Action(Action.Operation.ADD, items, result);
        solutionShelf.doAction(a);
        return result.getResult();
    }

    @Override
    public boolean removeItems(SolutionShelf solutionShelf, Set<SolutionItem> items) {
        SolutionResult<Boolean> result = new SolutionResult<Boolean>();
        Action a = new Action(Action.Operation.REMOVE, items, result);
        solutionShelf.doAction(a);
        return result.getResult();
    }

    @Override
    public boolean moveItems(SolutionShelf from, SolutionShelf to, Set<SolutionItem> items) {
        if (this.getContents(from).containsAll(items)) {
            Set<SolutionItem> currentItems = this.getContents(to);
            for (Item item : items) {
                if (currentItems.contains(item)) {
                    return false;
                }
            }

            SolutionResult<Boolean> result1 = new SolutionResult<Boolean>();
            Action a = new Action(Action.Operation.REMOVE, items, result1);

            from.doAction(a);
            if (result1.getResult()) {

                SolutionResult<Boolean> result2 = new SolutionResult<Boolean>();
                Action a2 = new Action(Action.Operation.ADD, items, result2);

                to.doAction(a2);
                    if(result2.getResult()){
                       return  true;
                    }
                    while (true){
                        SolutionResult<Boolean> result3 = new SolutionResult<Boolean>();
                        Action a3 = new Action(Action.Operation.ADD, items, result3);
                        from.doAction(a3);
                        if(result3.getResult()){
                            break;
                        }
                        else{
                            continue;
                        }
                    }
                    return false;

            }
            return false;
        }
        return false;
    }

    @Override
    public Set<SolutionItem> getContents() {
        Set<SolutionItem> results = new HashSet<>();
        for (SolutionShelf solutionShelf : this.solutionShelves) {
            SolutionResult<Set<SolutionItem>> result = new SolutionResult<Set<SolutionItem>>();
            Action a = new Action(Action.Operation.CONTENTS, new HashSet<>(), result);
            solutionShelf.doAction(a);
            results.addAll(result.getResult());
        }
        return results;
    }

    @Override
    public Set<SolutionItem> getContents(SolutionShelf solutionShelf) {
        SolutionResult<Set<SolutionItem>> result = new SolutionResult<Set<SolutionItem>>();
        Action a = new Action(Action.Operation.CONTENTS, new HashSet<>(), result);
        solutionShelf.doAction(a);
        return result.getResult();
    }

    @Override
    public Result<Boolean> addItemsAsync(SolutionShelf solutionShelf, Set<SolutionItem> items) {
        SolutionResult<Boolean> result = new SolutionResult<Boolean>();
        Action a = new Action(Action.Operation.ADD, items, result);
        solutionShelf.doAction(a);
        return result;
    }

    @Override
    public Result<Boolean> removeItemsAsync(SolutionShelf solutionShelf, Set<SolutionItem> items) {
        SolutionResult<Boolean> result = new SolutionResult<Boolean>();
        Action a = new Action(Action.Operation.REMOVE, items, result);
        solutionShelf.doAction(a);
        return result;
    }

    @Override
    public Result<Boolean> moveItemsAsync(SolutionShelf from, SolutionShelf to, Set<SolutionItem> items) {
        return new SolutionResult<Boolean>() {

            public Boolean getResult() {
                if (this.isReady())
                    return this.get();
                SolutionResult<Set<SolutionItem>> result1 = new SolutionResult<Set<SolutionItem>>();
                Action a = new Action(Action.Operation.CONTENTS, new HashSet<>(), result1);
                from.doAction(a);
                Set<SolutionItem> fromItems = result1.getResult();
                SolutionResult<Set<SolutionItem>> result2 = new SolutionResult<Set<SolutionItem>>();
                Action a1 = new Action(Action.Operation.CONTENTS, new HashSet<>(), result2);
                to.doAction(a1);
                Set<SolutionItem> toItems = result2.getResult();
                if (fromItems.containsAll(items)) {
                    for (Item item : items) {
                        if (toItems.contains(item)) {
                            return false;
                        }
                    }
                    SolutionResult<Boolean> result3 = new SolutionResult<Boolean>();
                    Action a2 = new Action(Action.Operation.REMOVE, items, result3);

                    from.doAction(a2);
                    if (result3.getResult()) {

                        SolutionResult<Boolean> result4 = new SolutionResult<Boolean>();
                        Action a3 = new Action(Action.Operation.ADD, items, result4);

                        to.doAction(a3);
                        if(result4.getResult()){
                            return  true;
                        }
                        while (true){
                            SolutionResult<Boolean> result5 = new SolutionResult<Boolean>();
                            Action a4 = new Action(Action.Operation.ADD, items, result5);
                            from.doAction(a4);
                            if(result5.getResult()){
                                break;
                            }
                            else{
                                continue;
                            }
                        }
                        return false;

                    }
                    return false;
                }
                return false ;
            }
        };
    }


    @Override
    public Result<Set<SolutionItem>> getContentsAsync() {

        Set<SolutionResult<Set<SolutionItem>>> results = new HashSet<>();
        for (SolutionShelf solutionShelf : this.solutionShelves) {
            SolutionResult<Set<SolutionItem>> result = new SolutionResult<Set<SolutionItem>>();
            Action a = new Action(Action.Operation.CONTENTS, new HashSet<>(), result);
            solutionShelf.doAction(a);
            results.add(result);
        }
        return new SolutionResult<Set<SolutionItem>>() {

            public Set<SolutionItem> getResult() {
                if (this.isReady())
                    return this.get();

                Set<SolutionItem> items = new HashSet<>();
                for (SolutionResult<Set<SolutionItem>> result : results) {
                    items.addAll(result.getResult());
                }

                return items;
            }
        };

    }

    @Override
    public Result<Set<SolutionItem>> getContentsAsync(SolutionShelf solutionShelf) {
        SolutionResult<Set<SolutionItem>> result = new SolutionResult<Set<SolutionItem>>();
        Action a = new Action(Action.Operation.CONTENTS, new HashSet<>(), result);
        solutionShelf.doAction(a);
        return result;
    }
}
