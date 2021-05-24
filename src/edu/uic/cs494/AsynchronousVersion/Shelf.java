package edu.uic.cs494.AsynchronousVersion;

import java.util.HashSet;
import java.util.Set;

public abstract class Shelf<I extends Item> implements Runnable {
    /*default*/ final HashSet<I> items = new HashSet<>();
    public final Thread allowedThread;

    public Shelf() {
        synchronized (this) {
            this.allowedThread = new Thread(this);
            this.allowedThread.setDaemon(true);
        }
    }

    /*default*/ Shelf startThread() {
        this.allowedThread.start();
        return this;
    }

    public void addItems(Set<I> items) {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        this.items.addAll(items);
    }

    public void removeItems(Set<I> items) {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        this.items.removeAll(items);
    }

    public final Set<I> getContents() {
        if (this.allowedThread.isAlive() && Thread.currentThread() != this.allowedThread)
            throw new Error("Wrong thread!");

        return new HashSet<>(this.items);
    }

    public final void run() {
        while (true) {
            Action a = getAction();

            switch (a.operation) {
                case ADD:
                    add(a.operand, a.result);
                    break;
                case REMOVE:
                    remove(a.operand, a.result);
                    break;
                case CONTENTS:
                    contents(a.result);
                    break;
                default:
                    throw new Error("Unknown operation");
            }
        }
    }

    protected abstract void doAction(Action a);

    protected abstract Action getAction();

    protected abstract void add(Set<I> items, Result<Boolean> result);

    protected abstract void remove(Set<I> items, Result<Boolean> result);

    protected abstract void contents(Result<Set<I>> result);

}
