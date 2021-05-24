package edu.uic.cs494.CustomReaderWritterLock;

import java.util.Objects;

public final class Action<T> {
    public enum Direction { IN , OUT };

    private final Direction direction;
    private final T t;

    public Action(Direction direction, T t) {
        this.direction = direction;
        this.t = t;
    }

    public T get() {
        return this.t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action<?> action = (Action<?>) o;
        return direction == action.direction &&
                Objects.equals(t, action.t);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, t);
    }

    @Override
    public String toString() {
        return "{" + (direction == Direction.IN ? "IN " : "OUT ") + t + '}';
    }
}
