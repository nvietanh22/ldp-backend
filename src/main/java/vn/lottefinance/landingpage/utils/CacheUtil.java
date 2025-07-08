package vn.lottefinance.landingpage.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cglib.core.internal.LoadingCache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CacheUtil {
    public static Cache<String, Object> cache1d = CacheBuilder.newBuilder().maximumSize(100000).expireAfterAccess(1, TimeUnit.DAYS).build();

    public static void pushCache(String key, Object data) {
        try {
            cache1d.put(key, data);
        } catch (Exception e) {
            log.error("Error push cache: " + key);
        }
    }

    public static Object getCache(String key) {
        try {
            Object objCahce = cache1d.getIfPresent(key);
            return objCahce;
        } catch (Exception e) {
            log.error("Error get cache");
            return null;
        }
    }

    public static void updateCache(String key, Object data) {
        cache1d.invalidate(key);
        cache1d.put(key, data);
    }
}

