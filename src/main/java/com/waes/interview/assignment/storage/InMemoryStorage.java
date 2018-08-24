package com.waes.interview.assignment.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In memory implementation of a trivial storage system backed up by a {@link ConcurrentHashMap ConcurrentHashMap} implementation.
 *
 * @param <T> Type to store
 * @author Juan Krzemien
 */
public class InMemoryStorage<T> implements Storage<T> {

  private final Map<String, T> inMemory = new ConcurrentHashMap<>();

  /**
   * Stores a value under a particular (unique) key.
   *
   * @param key   Key to store the value under. If not unique, existing value gets overwritten
   * @param value Value to store
   */
  @Override
  public void set(String key, T value) {
    inMemory.put(key, value);
  }

  /**
   * Retrieves an existing value, given its key.
   *
   * @param key Key of the value to retrieve
   * @return Stored value for provided key, or null if key does not exists.
   */
  @Override
  public T get(String key) {
    return inMemory.get(key);
  }

  /**
   * Check for a key existence
   *
   * @param key Key to check for existence
   * @return true if key exists in storage, false otherwise
   */
  @Override
  public boolean hasEntry(String key) {
    return inMemory.containsKey(key);
  }

  /**
   * Removes an entry from storage, given its key
   *
   * @param key Key of the entry to remove
   * @return Value of the removed entry
   */
  @Override
  public T remove(String key) {
    return inMemory.remove(key);
  }

}
