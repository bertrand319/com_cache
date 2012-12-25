
package com.baidu.common.cache.disc.impl;

import com.baidu.common.cache.disc.BaseDiscCache;
import com.baidu.common.cache.disc.DiscCacheAware;
import com.baidu.common.cache.disc.naming.FileNameGenerator;
import com.baidu.common.cache.disc.naming.Md5FileNameGenerator;

import java.io.File;

/**
 * Default implementation of {@linkplain DiscCacheAware disc cache}. Cache size
 * is unlimited.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseDiscCache
 * @modify huangweigan Use md5 file name generator
 */
public class UnlimitedDiscCache extends BaseDiscCache {
    /**
     * @param cacheDir Directory for file caching
     */
    public UnlimitedDiscCache(File cacheDir) {
        this(cacheDir, new Md5FileNameGenerator());
    }

    /**
     * @param cacheDir Directory for file caching
     * @param fileNameGenerator Name generator for cached files
     */
    public UnlimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, fileNameGenerator);
    }

    @Override
    public void put(String key, File file) {
        // Do nothing
    }
}
