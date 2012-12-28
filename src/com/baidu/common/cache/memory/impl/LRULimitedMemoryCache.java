
package com.baidu.common.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.baidu.common.cache.memory.LimitedMemoryCache;

/**
 * Limited {@link Object Object} cache. Provides {@link Object Objects} storing.
 * Size of all stored Objects will not to exceed size limit. When cache reaches
 * limit size then the least recently used Object is deleted from cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class LRULimitedMemoryCache extends LimitedMemoryCache<String, Object> {

    private static final int INITIAL_CAPACITY = 10;
    private static final float LOAD_FACTOR = 1.1f;

    /** Cache providing Least-Recently-Used logic */
    private final Map<String, Object> lruCache = Collections
            .synchronizedMap(new LinkedHashMap<String, Object>(INITIAL_CAPACITY, LOAD_FACTOR, true));

    public LRULimitedMemoryCache(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    public boolean put(String key, Object value) {
        if (super.put(key, value)) {
            lruCache.put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object get(String key) {
        lruCache.get(key); // call "get" for LRU logic
        return super.get(key);
    }

    @Override
    public void remove(String key) {
        lruCache.remove(key);
        super.remove(key);
    }

    @Override
    public void clear() {
        lruCache.clear();
        super.clear();
    }

    @Override
    protected int getSize(Object value) {
        return getObjectSize(value);
    }

    @Override
    protected Object removeNext() {
        Object mostLongUsedValue = null;
        synchronized (lruCache) {
            Iterator<Entry<String, Object>> it = lruCache.entrySet().iterator();
            if (it.hasNext()) {
                Entry<String, Object> entry = it.next();
                mostLongUsedValue = entry.getValue();
                it.remove();
            }
        }
        return mostLongUsedValue;
    }

    @Override
    protected Reference<Object> createReference(Object value) {
        return new WeakReference<Object>(value);
    }
}
