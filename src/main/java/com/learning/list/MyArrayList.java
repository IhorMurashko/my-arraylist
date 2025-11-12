package com.learning.list;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A custom implementation of an ArrayList data structure.
 * Provides dynamic array functionality with automatic resizing.
 *
 * @param <E> the type of elements in this list
 */
public class MyArrayList<E> implements Iterable<E> {
    /**
     * Default initial capacity of the ArrayList
     */
    private static final int DEFAULT_CAPACITY = 10;
    /**
     * Array buffer into which the elements are stored
     */
    private Object[] elementData;
    /**
     * The size of the ArrayList (number of elements it contains)
     */
    private int size = 0;
    /**
     * Counter for modifications to support fail-fast iteration
     */
    private int modCount = 0;

    /**
     * Constructs an empty list with default initial capacity
     */
    public MyArrayList() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    /**
     * Constructs an empty list with the specified initial capacity
     *
     * @param capacity the initial capacity of the list
     * @throws IllegalArgumentException if capacity is negative or too large
     */
    public MyArrayList(int capacity) {
        if (capacity < 0 || capacity > Integer.MAX_VALUE - 8) {
            throw new IllegalArgumentException("Invalid array capacity");
        }
        elementData = new Object[capacity];
    }

    /**
     * Returns the number of elements in this list
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list
     *
     * @param e element to be appended to this list
     * @return true if the element was added successfully
     */
    public boolean add(E e) {
        add(e, size);
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this list
     *
     * @param index index at which the element is to be inserted
     * @param e     element to be inserted
     * @return true if the element was added successfully
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public boolean add(int index, E e) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        if (isNeedingToGrow()) {
            grow();
        }
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        add(e, index);
        return true;
    }

    /**
     * Internal method to add element at specified index
     */
    private void add(E e, int index) {
        if (isNeedingToGrow()) {
            grow();
        }
        modCount++;
        size++;
        elementData[index] = e;
    }

    /**
     * Checks if array needs to grow
     */
    private boolean isNeedingToGrow() {
        return size == elementData.length;
    }

    /**
     * Increases the capacity of the array
     */
    private void grow() {
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1) + 1;
        if (newCapacity - oldCapacity < 0) {
            newCapacity = Integer.MAX_VALUE - 8;
        }
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    /**
     * Returns the element at the specified position in this list
     *
     * @param index index of the element to return
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return (E) elementData[index];
    }

    /**
     * Replaces the element at the specified position in this list
     *
     * @param index index of element to replace
     * @param e     element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public E set(int index, E e) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        E oldValue = (E) elementData[index];
        elementData[index] = e;
        return oldValue;
    }

    /**
     * Removes the element at the specified position in this list
     *
     * @param index index of element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        E oldValue = (E) elementData[index];
        System.arraycopy(elementData, index + 1, elementData, index, size - index - 1);
        elementData[--size] = null;
        modCount++;
        return oldValue;
    }

    /**
     * Returns true if this list contains no elements
     *
     * @return true if this list contains no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all elements from this list
     */
    public void clear() {
        if (size == 0) {
            return;
        }
        Arrays.fill(elementData, null);
        modCount++;
        size = 0;
    }

    /**
     * Returns an iterator over the elements in this list
     *
     * @return an Iterator over the elements
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Iterator implementation for MyArrayList
     */
    private class Itr implements Iterator<E> {
        private int cursor;
        private int lastRet = -1;
        private int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public E next() {
            checkForComodification();
            int i = cursor;
            if (i >= size) {
                throw new NoSuchElementException();
            }
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            cursor = i + 1;
            return (E) elementData[lastRet = i];

        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException("No element to remove");
            }
            checkForComodification();
            try {
                MyArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}