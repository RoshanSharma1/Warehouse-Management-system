package edu.uic.cs494.CustomReaderWritterLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public abstract class CS494ReadWriteLock implements ReadWriteLock {

    protected abstract void doReadLock();

    protected abstract void doReadUnlock();

    protected abstract void doWriteLock();

    protected abstract void doWriteUnlock();

    @Override
    public final Lock readLock() {
        return new Lock() {
            @Override
            public void lock() {
                doReadLock();
            }

            @Override
            public void unlock() {
                doReadUnlock();
            }

            @Override
            public void lockInterruptibly() throws InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean tryLock() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Condition newCondition() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public final Lock writeLock() {
        return new Lock() {
            @Override
            public void lock() {
                doWriteLock();
            }

            @Override
            public void unlock() {
                doWriteUnlock();
            }

            @Override
            public void lockInterruptibly() throws InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean tryLock() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Condition newCondition() {
                throw new UnsupportedOperationException();
            }
        };
    }

}
