package edu.uic.cs494.SynchronousVersion;

public interface Shelf {
    public void getReadLock();
    public void getWriteLock();
    public void unlockRead();
    public void unlockWrite();
}
