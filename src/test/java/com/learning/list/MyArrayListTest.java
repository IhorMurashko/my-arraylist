package com.learning.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;


class MyArrayListTest {
    private final String elementDataFieldName = "elementData";
    private final String modCountFieldName = "modCount";
    private MyArrayList<Integer> list;

    @BeforeEach
    void setUp() {
        list = new MyArrayList<>();
    }

    /**
     * constructor constraints:
     * constructor with int value less than 1 or greater than Integer.MAX_VALUE-8 -> throw IllegalArgumentException;
     * empty constructor -> default array size;
     * constructor with int value between 1 and Integer.MAX_VALUE-8 -> new array with given size;
     */
    @Nested
    class CapacityValidatorTest {

        @ParameterizedTest
        @ValueSource(ints = {-1, -15, Integer.MIN_VALUE, Integer.MAX_VALUE - 7, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 5})
        void shouldThrowIllegalArgumentException_when_constructorValueIsLessThanZero(int capacity) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new MyArrayList<>(capacity));
        }

        @Test
        void shouldCreateDefaultArrayLength_when_constructorIsEmpty() throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<?> list = new MyArrayList<>();

            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);

            assertEquals(10, internalArray.length);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10, 59, 712, 10056, 56900, 123456789,})
        void shouldCreateInstanceWithValidArrayLength(int capacity) throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<?> list = new MyArrayList<>(capacity);

            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);

            assertEquals(capacity, internalArray.length);
        }
    }

    /**
     * method ADD constraints:
     * if size == capacity -> grow an array by 50% and add an object;
     * if adding an object without array position -> the object will be added to position size, increase size by 1;
     * if adding an object with array position -> the object will be added to position, increase size by 1, all objects
     * from size to array position will be shifted;
     */
    @Nested
    class TestingAddArrayMethod {

        @ParameterizedTest
        @ValueSource(ints = {1, 15, 65, 5, 789632, -265, 0})
        void shouldAddObjectToPositionAndIncreaseSize_when_addingObjectWithoutArrayPosition(int val) throws NoSuchFieldException, IllegalAccessException {
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);

            assertTrue(list.add(val));

            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(1, list.size());
            assertEquals(val, internalArray[0]);
            assertEquals(1, modCount);
        }

        @Test
        void shouldAddObjectsToTailArray() throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<Integer> list = new MyArrayList<>();
            Integer[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);

            for (Integer val : array) {
                assertTrue(list.add(val));
            }

            for (int i = 0; i < 10; i++) {
                assertEquals(array[i], internalArray[i]);
            }
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(10, internalArray.length);
            assertEquals(10, list.size());
            assertEquals(10, modCount);
        }

        @Test
        void shouldAddNullValueToArray() throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<Integer> list = new MyArrayList<>();
            Integer[] array = {1, 2, 3, 4, 5, null, null, 8, 9, 10};
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            for (Integer val : array) {
                assertTrue(list.add(val));
            }
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(10, internalArray.length);
            assertEquals(10, list.size());
            assertEquals(10, modCount);
        }

        @Test
        void shouldGrowArrayBy50PercentAndAddObjectToTailArray() throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            Integer[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            int expectedLength = calculateExpectedCapacity(initCapacity, array.length);

            assertEquals(initCapacity, internalArray.length);
            assertEquals(0, list.size());
            for (Integer integer : array) {
                assertTrue(list.add(integer));
            }
            internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            for (int i = 0; i < array.length; i++) {
                assertEquals(array[i], internalArray[i]);
            }
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(array.length, modCount);
            assertEquals(expectedLength, internalArray.length);
            assertEquals(array.length, list.size());
        }


        @Test
        void addValueInsideTheArray() throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 15;
            int addingValue = 11;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            Integer[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
            int expectedLength = calculateExpectedCapacity(15, array.length + 1);
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            for (Integer val : array) {
                assertTrue(list.add(val));
            }
            assertTrue(list.add(4, addingValue));
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(expectedLength, internalArray.length);
            assertEquals(array.length + 1, list.size());
            assertEquals(array.length + 1, modCount);
            assertEquals(addingValue, internalArray[4]);
            for (int i = 0; i < 4; i++) {
                assertEquals(array[i], internalArray[i]);
            }
            for (int i = 5; i < 11; i++) {
                assertEquals(array[i - 1], internalArray[i]);
            }
        }

        @Test
        void shouldThrowIndexOfBoundException_when_addingIndexGreaterArraySize() throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<Integer> list = new MyArrayList<>(5);
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            Integer addingValue = 123;
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, addingValue));
            assertEquals(0, list.size());
            assertEquals(5, internalArray.length);
        }

        @Test
        void shouldThrowIndexOfBoundException_when_addingIndexLessZero() throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<Integer> list = new MyArrayList<>(5);
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            Integer addingValue = 123;
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, addingValue));
            assertEquals(0, list.size());
            assertEquals(5, internalArray.length);
        }

    }

    @Nested
    class TestingGetArrayMethod {
        @ParameterizedTest
        @ValueSource(ints = {-10, -5, -100, -1, 15, 30, 10, 500, 2000})
        void shouldThrowIndexOutOfBoundException_when_indexIsWrong(int index) throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            int[] array = {1, 2, 3, 4, 5};
            for (int val : array) {
                assertTrue(list.add(val));
            }
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(index));
            assertEquals(initCapacity, list.size());
            assertEquals(initCapacity, internalArray.length);
            assertEquals(array.length, modCount);
        }

        @Test
        void shouldReturnValueByIndex() throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;

            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            int[] array = {1, 2, 3, 4, 5};
            for (int val : array) {
                assertTrue(list.add(val));
            }
            Integer expectedValue = list.get(0);
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(array[0], expectedValue);
            assertEquals(initCapacity, list.size());
            assertEquals(initCapacity, internalArray.length);
            assertEquals(array.length, modCount);
        }


    }

    @Nested
    class TestingSetMethod {

        @ParameterizedTest
        @ValueSource(ints = {-10, -5, -100, -1, 15, 30, 10, 500, 2000})
        void shouldThrowIndexOutOfBoundException_when_indexIsWrong(int index) throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            int val = 10;
            assertThrows(IndexOutOfBoundsException.class, () -> list.add(index, val));
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(initCapacity, internalArray.length);
            assertEquals(0, modCount);
            assertEquals(0, list.size());

        }

        @Test
        void shouldSetValueToArrayIndexAndReturnOldValue() throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;
            int newValue = 10;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            int[] array = {1, 2, 3, 4, 5};
            for (int val : array) {
                assertTrue(list.add(val));
            }
            Integer expected = list.set(0, newValue);
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(array[0], expected);
            assertEquals(newValue, list.get(0));
            assertEquals(array.length, list.size());
            assertEquals(array.length, modCount);
            for (int i = 1; i < array.length; i++) {
                assertEquals(array[i], list.get(i));
            }
        }

    }

    @Nested
    class TestingRemoveMethod {
        @ParameterizedTest
        @ValueSource(ints = {-10, -5, -100, -1, 15, 30, 10, 500, 2000})
        void shouldThrowIndexOutOfBoundException_when_indexIsWrong(int index) throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            assertThrows(IndexOutOfBoundsException.class, () -> list.remove(index));
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(initCapacity, internalArray.length);
            assertEquals(0, modCount);
            assertEquals(0, list.size());
        }

        @Test
        void shouldReturnRemovedValue() throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            int[] array = {1, 2, 3, 4, 5};
            for (int val : array) {
                assertTrue(list.add(val));
            }
            Integer expectedRemovedElement = list.remove(0);
            assertEquals(expectedRemovedElement, array[0]);
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(array.length + 1, modCount);
            assertEquals(array.length - 1, list.size());
            for (int i = 1; i < array.length; i++) {
                assertEquals(array[i], list.get(i - 1));
            }
        }
    }

    @Nested
    class TestingClearMethod {
        @Test
        void shouldClearList() throws NoSuchFieldException, IllegalAccessException {
            int initCapacity = 5;
            MyArrayList<Integer> list = new MyArrayList<>(initCapacity);
            int[] array = {1, 2, 3, 4, 5};
            for (int val : array) {
                assertTrue(list.add(val));
            }
            list.clear();
            Object[] internalArray = (Object[]) getFieldValue(list, elementDataFieldName);
            Integer modCount = (Integer) getFieldValue(list, modCountFieldName);
            assertEquals(array.length + 1, modCount);
            assertEquals(0, list.size());
            for (Object o : internalArray) {
                assertNull(o);
            }
        }
    }

    @Nested
    class TestingIsEmptyMethod {
        @Test
        void shouldReturnTrue_when_listIsEmpty() throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<Integer> list = new MyArrayList<>();
            assertTrue(list.isEmpty());
        }

        @Test
        void shouldReturnFalse_when_listIsNotEmpty() throws NoSuchFieldException, IllegalAccessException {
            MyArrayList<Integer> list = new MyArrayList<>();
            list.add(1);
            assertFalse(list.isEmpty());
        }
    }

    @Nested
    class BasicIterationTests {

        @Test
        void testEmptyListIteration() {
            Iterator<Integer> it = list.iterator();

            assertFalse(it.hasNext(), "hasNext() returns false for empty list");
            assertThrows(NoSuchElementException.class, it::next,
                    "next() has throw NoSuchElementException");
        }

        @Test
        void testSingleElementIteration() {
            list.add(1);
            Iterator<Integer> it = list.iterator();

            assertTrue(it.hasNext());
            assertEquals(1, it.next());
            assertFalse(it.hasNext());
        }

        @Test
        void testFullIteration() {
            list.add(1);
            list.add(2);
            list.add(3);

            Iterator<Integer> it = list.iterator();

            assertTrue(it.hasNext());
            assertEquals(1, it.next());

            assertTrue(it.hasNext());
            assertEquals(2, it.next());

            assertTrue(it.hasNext());
            assertEquals(3, it.next());

            assertFalse(it.hasNext());
        }

        @Test
        void testHasNextDoesNotMoveCursor() {
            list.add(1);
            list.add(2);

            Iterator<Integer> it = list.iterator();

            assertTrue(it.hasNext());
            assertTrue(it.hasNext());
            assertTrue(it.hasNext());

            assertEquals(1, it.next(), "cursor has moved");
        }

        @Test
        void testNextAfterEnd() {
            list.add(1);
            Iterator<Integer> it = list.iterator();

            it.next();
            assertFalse(it.hasNext());

            assertThrows(NoSuchElementException.class, it::next);
        }

        @Test
        void testMultipleNextAfterEnd() {
            list.add(1);
            Iterator<Integer> it = list.iterator();

            it.next();
            assertThrows(NoSuchElementException.class, it::next);
            assertThrows(NoSuchElementException.class, it::next);
        }
    }

    private Object getFieldValue(MyArrayList<?> list, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = list.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(list);
    }

    private int calculateExpectedCapacity(int initialCapacity, int elementsToAdd) {
        int capacity = initialCapacity;
        int currentSize = 0;
        for (int i = 0; i < elementsToAdd; i++) {
            if (currentSize == capacity) {
                capacity = capacity + (capacity >> 1) + 1;
            }
            currentSize++;
        }
        return capacity;
    }
}