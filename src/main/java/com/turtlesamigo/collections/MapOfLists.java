package com.turtlesamigo.collections;

import java.util.HashMap;
import java.util.List;

/**
 * A map that maps keys to lists of values.
 * @param <K> The type of the keys.
 * @param <V> The type of the values.
 */
public class MapOfLists<K, V> extends HashMap<K, List<V>> {
    /**
     * Adds a value to the list of values corresponding to the given key.
     * If the key is not present in the map, a new list is created.
     * @param key The key.
     * @param value The value.
     */
    public void add(K key, V value) {
        if (containsKey(key)) {
            get(key).add(value);
        } else {
            put(key, List.of(value));
        }
    }

    /**
     * Adds a list of values to the list of values corresponding to the given key.
     * If the key is not present in the map, a new list is created.
     * @param key The key.
     * @param values The list of values.
     */
    public void addAll(K key, List<V> values) {
        if (containsKey(key)) {
            get(key).addAll(values);
        } else {
            put(key, values);
        }
    }

    /**
     * Removes a value from the list of values corresponding to the given key.
     * @param key The key.
     * @param value The value.
     */
    public void removeElement(K key, V value) {
        if (containsKey(key)) {
            get(key).remove(value);
        }
    }
}
