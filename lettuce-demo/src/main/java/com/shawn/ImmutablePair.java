package com.shawn;

import java.util.Map.Entry;

public final class ImmutablePair<L, R> extends Pair<L, R> {
    public static final ImmutablePair<?, ?>[] EMPTY_ARRAY = new ImmutablePair[0];
    private static final ImmutablePair NULL = of(null, null);
    private static final long serialVersionUID = 4954918890077093842L;
    public final L left;
    public final R right;

    public static <L, R> ImmutablePair<L, R>[] emptyArray() {
        return (ImmutablePair[]) EMPTY_ARRAY;
    }

    public static <L, R> Pair<L, R> left(L left) {
        return of(left, null);
    }

    public static <L, R> ImmutablePair<L, R> nullPair() {
        return NULL;
    }

    public static <L, R> ImmutablePair<L, R> of(L left, R right) {
        return new ImmutablePair<L, R>(left, right);
    }

    public static <L, R> ImmutablePair<L, R> of(Entry<L, R> pair) {
        L left = null;
        R right = null;
        if (pair != null) {
            left = pair.getKey();
            right = pair.getValue();
        }

        return new ImmutablePair(left, right);
    }

    public static <L, R> Pair<L, R> right(R right) {
        return of(null, right);
    }

    public ImmutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return this.left;
    }

    public R getRight() {
        return this.right;
    }

    public R setValue(R value) {
        throw new UnsupportedOperationException();
    }
}
