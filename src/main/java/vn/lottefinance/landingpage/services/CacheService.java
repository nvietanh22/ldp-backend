package vn.lottefinance.landingpage.services;

import java.util.Optional;

public interface CacheService {
    /**
     * Get an item from the cache.
     *
     * @param key The key of the cache item.
     * @return The cached item, wrapped in an Optional.
     */
    Optional<String> getFromCache(String key);

    /**
     * Put an item into the cache.
     *
     * @param key The key for the cache item.
     * @param value The value to cache.
     */
    void putInCache(String key, String value);

    void deleteCache(String key);
}
