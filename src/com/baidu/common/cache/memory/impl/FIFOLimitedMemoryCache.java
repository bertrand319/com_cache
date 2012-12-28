
package com.baidu.common.cache.memory.impl;

import android.graphics.Bitmap;

import com.baidu.common.cache.memory.LimitedMemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Limited {@link Object Object} cache. Provides {@link Object Objects} storing.
 * Size of all stored Objects will not to exceed size limit. When cache reaches
 * limit size then cache clearing is processed by FIFO principle.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @modify huangweigan
 */
public class FIFOLimitedMemoryCache extends LimitedMemoryCache<String, Object> {

    /*
     * 1 Mb
     */
    public static final int DEFAULT_SIZE_LIMIT = 1024 * 1024;

    private final List<Object> queue = Collections.synchronizedList(new LinkedList<Object>());

    public FIFOLimitedMemoryCache(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    public boolean put(String key, Object value) {
        if (super.put(key, value)) {
            queue.add(value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void remove(String key) {
        Object value = super.get(key);
        if (value != null) {
            queue.remove(value);
        }
        super.remove(key);
    }

    @Override
    public void clear() {
        queue.clear();
        super.clear();
    }

    @Override
    protected int getSize(Object value) {
        return getObjectSize(value);
    }

    @Override
    protected Object removeNext() {
        return queue.remove(0);
    }

    @Override
    protected Reference<Object> createReference(Object value) {
        return new WeakReference<Object>(value);
    }
}
