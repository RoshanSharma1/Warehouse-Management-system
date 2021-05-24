package edu.uic.cs494.CustomReaderWritterLock.solution;

import edu.uic.cs494.CustomReaderWritterLock.CS494ReadWriteLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SolutionReadWriteLock extends CS494ReadWriteLock {
    int readerIn;
    int readerOut;
    boolean writerActive;
    Lock lock;
    Condition condition;
    public SolutionReadWriteLock(){
        readerIn = 0;
        readerOut = 0;
        writerActive = false;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    @Override
    protected void doReadLock() {
            lock.lock();
            try {
                while (writerActive) {
                    try {
                        condition.await();

                    } catch (InterruptedException ex) {
                        continue;
                    }

                }
                readerIn++;

            } finally {
                lock.unlock();
            }

    }

    @Override
    protected void doReadUnlock() {
        lock.lock();
        try{
            readerOut++;
            if (readerIn == readerOut) {
                condition.signalAll();
            }

        }
        finally {
            lock.unlock();
        }
    }

    @Override
    protected void doWriteLock() {
            lock.lock();
            try {

                while (writerActive) {
                    try {
                        condition.await();

                    } catch (InterruptedException ex) {
                      continue;
                    }

                }

                writerActive = true;
                while (readerIn != readerOut) {
                    try {
                        condition.await();

                    } catch (InterruptedException ex) {
                     continue;
                    }

                }


            } finally {
                lock.unlock();
            }


    }

    @Override
    protected void doWriteUnlock() {
        lock.lock();
        try{
            writerActive = false;
            condition.signalAll();

        }
        finally {
            lock.unlock();
        }
    }
}
