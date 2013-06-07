package rest.o.gram.data_structs;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 5/25/13
 */
public interface IDictionary<K, V> extends Iterable<V> {
    /**
     * Attempts to insert given key and value to the front of this dictionary
     * Return true if successful, false otherwise
     */
    boolean putFirst(K key, V value);

    /**
     * Attempts to insert given key and value to the back of this dictionary
     * Return true if successful, false otherwise
     */
    boolean putLast(K key, V value);

    /**
     * Attempts to find value according to given key
     * Returns value if successful, null otherwise
     */
    V find(final K key);

    /**
     * Return true whether given key exists in the dictionary, false otherwise
     */
    boolean contains(K key);

    /**
     * Attempts to remove item with given key from this dictionary
     * Return true if successful, false otherwise
     */
    boolean remove(K key);

    /**
     * Clears all items
     */
    void clear();

    /**
     * Returns the size of this dictionary
     */
    int size();

    /**
     * Return true whether this dictionary is empty, false otherwise
     */
    boolean isEmpty();
}
