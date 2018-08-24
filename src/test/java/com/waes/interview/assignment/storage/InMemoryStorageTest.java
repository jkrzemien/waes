package com.waes.interview.assignment.storage;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Regular JUnit 4 test suite for {@link InMemoryStorage InMemoryStorage} class.
 */
public class InMemoryStorageTest {

  private static final String KEY = "KEY";
  private static final String ANOTHER_KEY = "ANOTHER_KEY";
  private static final String VALUE = "VALUE";

  private InMemoryStorage<String> inMemoryStorage = new InMemoryStorage<>();

  @Test
  public void setAndRetrieveValue() {
    inMemoryStorage.set(KEY, VALUE);
    assertThat("Value was set into storage for given key", inMemoryStorage.get(KEY), is(VALUE));
  }

  @Test(expected = NullPointerException.class)
  public void settingNullValueThrowsNullPointerException() {
    inMemoryStorage.set(KEY, null);
  }

  @Test(expected = NullPointerException.class)
  public void settingNullKeyThrowsNullPointerException() {
    inMemoryStorage.set(null, VALUE);
  }

  @Test
  public void hasEntrySuccessful() {
    inMemoryStorage.set(KEY, VALUE);
    assertThat("Value was set into storage for given key", inMemoryStorage.hasEntry(KEY), is(true));
  }

  @Test
  public void hasEntryUnsuccessful() {
    assertThat("Value was set into storage for given key", inMemoryStorage.hasEntry(ANOTHER_KEY), is(false));
  }

  @Test
  public void remove() {
    inMemoryStorage.set(KEY, VALUE);
    assertThat("Value is set in storage for given key", inMemoryStorage.hasEntry(KEY), is(true));
    String removedValue = inMemoryStorage.remove(KEY);
    assertThat("Value removed is correct", removedValue, is(VALUE));
    assertThat("Entry no longer exists in storage for given key", inMemoryStorage.hasEntry(KEY), is(false));
  }

}