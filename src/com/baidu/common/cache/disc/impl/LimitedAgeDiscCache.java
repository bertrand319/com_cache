
package com.baidu.common.cache.disc.impl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.baidu.common.cache.disc.BaseDiscCache;
import com.baidu.common.cache.disc.naming.FileNameGenerator;
import com.baidu.common.cache.disc.naming.Md5FileNameGenerator;

/**
 * Cache which deletes files which were loaded more than defined time. Cache
 * size is unlimited.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see BaseDiscCache
 * @modify huangweigan 
 *  	1. Add default max age value 
 *      2. Use md5 file name generator
 *      3. Delete the expired file when setting up loadingData
 */
public class LimitedAgeDiscCache extends BaseDiscCache {
	
	/*
	 * 7 days
	 */
	public static final long DEFAULT_MAX_AGE = 604800;

    private final long maxFileAge;

    private final Map<File, Long> loadingDates = Collections
            .synchronizedMap(new HashMap<File, Long>());

    /**
     * @param cacheDir Directory for file caching
     * @param maxAge Max file age (in seconds). If file age will exceed this
     *            value then it'll be removed on next treatment (and therefore
     *            be reloaded).
     */
    public LimitedAgeDiscCache(File cacheDir, long maxAge) {
        this(cacheDir, new Md5FileNameGenerator(), maxAge);
    }

    /**
     * @param cacheDir Directory for file caching
     * @param fileNameGenerator Name generator for cached files
     * @param maxAge Max file age (in seconds). If file age will exceed this
     *            value then it'll be removed on next treatment (and therefore
     *            be reloaded).
     */
    public LimitedAgeDiscCache(File cacheDir, FileNameGenerator fileNameGenerator, long maxAge) {
        super(cacheDir, fileNameGenerator);
        this.maxFileAge = maxAge * 1000; // to milliseconds
        readLoadingDates();
    }

    private void readLoadingDates() {
        File[] cachedFiles = getCacheDir().listFiles();
        for (File cachedFile : cachedFiles) {
        	long lastModifedDate = cachedFile.lastModified();
        	if(System.currentTimeMillis() - lastModifedDate > maxFileAge)
        	{
        		cachedFile.delete();
        	}
        	else 
        	{
        		loadingDates.put(cachedFile, cachedFile.lastModified());
			}
        }
    }

    @Override
    public void put(String key, File file) {
        long currentTime = System.currentTimeMillis();
        file.setLastModified(currentTime);
        loadingDates.put(file, currentTime);
    }

    @Override
    public File get(String key) {
        File file = super.get(key);
        if (file.exists()) {
            Long loadingDate = loadingDates.get(file);
            if (loadingDate == null) {
                loadingDate = file.lastModified();
            }
            if (System.currentTimeMillis() - loadingDate > maxFileAge) {
                file.delete();
                loadingDates.remove(file);
            }
        }
        return file;
    }
}
