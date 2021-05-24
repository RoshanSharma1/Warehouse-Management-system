package edu.uic.cs494.AsynchronousVersion.solution;

import edu.uic.cs494.AsynchronousVersion.Action;
import edu.uic.cs494.AsynchronousVersion.Result;
import edu.uic.cs494.AsynchronousVersion.Shelf;

import java.util.LinkedList;
import java.util.Set;

public class SolutionShelf extends Shelf<SolutionItem> {
    private  final int size;
    private LinkedList<Action> toDos = null;
    public  SolutionShelf(int size)
    {
        this.size = size;
        toDos = new LinkedList<>();
    }
    @Override
    protected  void doAction(Action a) {
       synchronized (toDos) {
           toDos.addLast(a);
           toDos.notifyAll();
       }


    }

    @Override
    protected  Action getAction() {
        synchronized (toDos) {
            while (toDos.isEmpty()) {
                try {
                    toDos.wait();
                } catch (InterruptedException e) {
                    continue;
                }
            }

            return toDos.remove();
        }

    }

    @Override
    protected void contents(Result result) {
        result.setResult(this.getContents());
    }

    @Override
    protected void remove(Set items, Result result) {
        if(!this.getContents().containsAll(items)){
            result.setResult(false);
            return;
        }
        this.removeItems(items);
        result.setResult(true);
    }

    @Override
    protected void add(Set items, Result result) {
        Set<SolutionItem> currentItems = this.getContents();
        if(this.size - currentItems.size() >= items.size()){
            for (Object item : items){
                    if(currentItems.contains(item)){
                        result.setResult(false);
                        return;
                    }
                }

            this.addItems (items);
            result.setResult(true);
            return;
        }
        result.setResult(false);

    }



}
