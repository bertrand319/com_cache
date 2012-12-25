
package com.baidu.common.cache.disc.impl;

import com.baidu.common.cache.disc.LimitedDiscCache;
import com.baidu.common.cache.disc.naming.FileNameGenerator;
import com.baidu.common.cache.disc.naming.Md5FileNameGenerator;

import java.io.File;

/**
 * Disc cache limited by total cache size. If cache size exceeds specified limit
 * then file with the most oldest last usage date will be deleted.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see LimitedDiscCache
 * @modify huangweigan Add default max size value and use md5 file name
 *         generator
 */
public class TotalSizeLimitedDiscCache extends LimitedDiscCache {

    /*
     * 1Mb
     */
    public static final int DEFAUL_MAX_SIZE = 1048576;

    /**
     * @param cacheDir Directory for file caching. <b>Important:</b> Specify
     *            separate folder for cached files. It's needed for right cache
     *            limit work.
     * @param maxCacheSize Maximum cache directory size (in bytes). If cache
     *            size exceeds this limit then file with the most oldest last
     *            usage date will be deleted.
     */
    public TotalSizeLimitedDiscCache(File cacheDir, int maxCacheSize) {
        this(cacheDir, new Md5FileNameGenerator(), maxCacheSize);
    }

    /**
     * @param cacheDir Directory for file caching. <b>Important:</b> Specify
     *            separate folder for cached files. It's needed for right cache
     *            limit work.
     * @param fileNameGenerator Name generator for cached files
     * @param maxCacheSize Maximum cache directory size (in bytes). If cache
     *            size exceeds this limit then file with the most oldest last
     *            usage date will be deleted.
     */
    public TotalSizeLimitedDiscCache(File cacheDir, FileNameGenerator fileNameGenerator,
            int maxCacheSize) {
        super(cacheDir, fileNameGenerator, maxCacheSize);
    }

    @Override
    protected int getSize(File file) {
        return (int) file.length();
    }
}
