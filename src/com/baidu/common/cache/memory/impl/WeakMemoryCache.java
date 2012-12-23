
package com.baidu.common.cache.memory.impl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import com.baidu.common.cache.memory2.BaseMemoryCache;

/**
 * Memory cache with {@linkplain WeakReference weak references} to
 * {@linkplain android.graphics.Bitmap bitmaps}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class WeakMemoryCache extends BaseMemoryCache<String, Object> {
    @Override
    protected Reference<Object> createReference(Object value) {
        return new WeakReference<Object>(value);
    }
}
