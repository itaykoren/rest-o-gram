package rest.o.gram.data_structs;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public class Dictionary<K, V> implements IDictionary<K, V>, Serializable {
    /**
     * Ctor
     */
    public Dictionary() {
        // Initialize containers
        map = new HashMap<>();
        deque = new ArrayDeque<>();
    }

    @Override
    public boolean putFirst(K key, V value) {
        if(contains(key))
            return false;

        // Insert to map
        map.put(key, value);

        // Insert to deque front
        deque.addFirst(value);
        return true;
    }

    @Override
    public boolean putLast(K key, V value) {
        if(contains(key))
            return false;

        // Insert to map
        map.put(key, value);

        // Insert to deque back
        deque.addLast(value);
        return true;
    }

    @Override
    public V find(final K key) {
        if(!contains(key))
            return null;

        return map.get(key);
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override
    public boolean remove(K key) {
        if(!contains(key))
            return false;

        // Get value
        V value = map.get(key);

        // Remove from map
        map.remove(key);

        // Remove from deque
        deque.remove(value);
        return true;
    }

    @Override
    public void clear() {
        map.clear();
        deque.clear();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public Iterator<V> iterator() {
        return deque.iterator();
    }

    private Map<K, V> map; // Internal map
    private Deque<V> deque; // Internal deque
}
