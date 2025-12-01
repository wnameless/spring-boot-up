package com.github.wnameless.spring.boot.up.notification;

import java.util.concurrent.locks.Lock;
import com.google.common.util.concurrent.Striped;

/**
 * Utility class that manages per-entity locks for notification callback operations. Uses Guava's
 * Striped locks to provide a fixed-size pool of locks with consistent hashing, which bounds memory
 * usage regardless of entity count and requires no cleanup.
 *
 * <p>
 * This is used to prevent race conditions in find-or-create operations where multiple threads might
 * simultaneously create duplicate records.
 * </p>
 */
public final class NotificationCallbackLockManager {

  // Fixed pool of 256 locks - memory is bounded, no cleanup needed
  // Different keys may map to the same lock (slight reduction in parallelism)
  private static final Striped<Lock> LOCKS = Striped.lock(256);

  private NotificationCallbackLockManager() {}

  /**
   * Gets a lock for the given key. Different keys may map to the same lock due to striping, but
   * this is acceptable as it only slightly reduces parallelism while providing bounded memory
   * usage.
   *
   * @param key the key to get a lock for (typically entity ID or composite key)
   * @return a Lock instance for the given key
   */
  public static Lock getLock(Object key) {
    return LOCKS.get(key);
  }

}
