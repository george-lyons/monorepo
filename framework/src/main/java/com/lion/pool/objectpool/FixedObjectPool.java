package com.lion.pool.objectpool;

import java.lang.reflect.Array;
import java.util.function.Supplier;

public class FixedObjectPool<T extends Mutable> implements  ObjectPool<T>{
    private final T [] objects;
    private int capacity;
    private int index;
    public FixedObjectPool(int capacity, Supplier<T> factory, Class<T> clazz) {
        this.objects = (T[]) Array.newInstance(clazz, capacity);
        this.capacity = capacity;
        this.index = capacity -1;
        for(int i = 0; i < objects.length; i++) {
            objects[i] = factory.get();
        }
    }

    public T borrow() {
        if(index < 0) {
            return null;
        } else {
            return objects[index--];
        }
    }

    public boolean release(T object) {
        if(index == (capacity -1)) {
            //exception
            return false;
        } else {
            object.reset();
            objects[++index] = object;
            return true;
        }
    }

    public int getRemaining() {
        return index + 1;
    }

    public int getCapacity() {
        return capacity;
    }

}
