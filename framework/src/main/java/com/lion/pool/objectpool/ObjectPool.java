package com.lion.pool.objectpool;

public interface ObjectPool<T extends Mutable> {
    T borrow();
    boolean release(T object);
}
