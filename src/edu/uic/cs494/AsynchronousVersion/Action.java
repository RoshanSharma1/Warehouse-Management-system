package edu.uic.cs494.AsynchronousVersion;

import java.util.Set;

public final class Action<I extends Item, R> {
    public enum Operation { ADD, REMOVE, CONTENTS }

    public final Operation operation;
    public final Set<I> operand;
    public final Result<R> result;

    public Action(Operation operation, Set<I> operand, Result<R> result) {
        this.operation = operation;
        this.operand = operand;
        this.result = result;
    }
}

