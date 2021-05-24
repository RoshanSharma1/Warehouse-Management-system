package edu.uic.cs494.CustomReaderWritterLock.solution;

import edu.uic.cs494.CustomReaderWritterLock.Action;
import edu.uic.cs494.CustomReaderWritterLock.Item;

import java.util.LinkedList;
import java.util.List;

public class ItemSolution implements Item {
    public  String description;
    public List<Action<ShelfSolution>> auditLogs;

    public ItemSolution(String description) {
        this.description = description;
        this.auditLogs = new LinkedList<>();
    }
}
