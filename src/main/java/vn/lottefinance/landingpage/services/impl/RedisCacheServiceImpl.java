package vn.lottefinance.landingpage.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import vn.lottefinance.landingpage.services.CacheService;

import java.util.Optional;

@Service
@Slf4j
public class RedisCacheServiceImpl implements CacheService {
    @Autowired
    private CacheManager cacheManager;
    @Override
    public Optional<String> getFromCache(String key) {
        log.info("getFromCache key: {}", key);
        Cache.ValueWrapper valueWrapper = getCache().get(key);
        if (valueWrapper != null) {
            return Optional.ofNullable((String) valueWrapper.get());
        }
        return Optional.empty();
    }

    @Override
    public void putInCache(String key, String value) {
        log.info("putInCache: {} - {}", key, value);
        getCache().put(key, value);
    }

    @Override
    public void deleteCache(String key) {
        getCache().evict(key);
    }

    private Cache getCache() {
        return cacheManager.getCache("myCache");
    }
}
