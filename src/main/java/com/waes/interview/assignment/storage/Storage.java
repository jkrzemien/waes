package com.waes.interview.assignment.storage;

/**
 * Defines generic storage operations for setting, retrieving, removing and checking for values.
 * <p>
 * It is just a very high level abstraction for things such as caches, databases, etc.
 * <p>
 * I did not want to use Map interface directly, but a more abstract restricted set of it.
 * Since not every storage has a table
 *
 * @param <T> Type to store
 * @author Juan Krzemien
 */
public interface Storage<T> {

  /**
   * Stores a value under a particular (unique) key.
   *
   * @param key   Key to store the value under. If not unique, existing value gets overwritten
   * @param value Value to store
   */
  void set(String key, T value);

  /**
   * Retrieves an existing value, given its key.
   *
   * @param key Key of the value to retrieve
   * @return Stored value for provided key, or null if key does not exists.
   */
  T get(String key);


  /**
   * Check for a key existence
   *
   * @param key Key to check for existence
   * @return true if key exists in storage, false otherwise
   */
  boolean hasEntry(String key);

  /**
   * Removes an entry from storage, given its key
   *
   * @param key Key of the entry to remove
   * @return Value of the removed entry
   */
  T remove(String key);
}
