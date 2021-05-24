package edu.uic.cs494.SynchronousVersion.solution;

import edu.uic.cs494.SynchronousVersion.Action;
import edu.uic.cs494.SynchronousVersion.Item;

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
