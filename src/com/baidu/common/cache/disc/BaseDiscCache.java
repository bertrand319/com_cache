
package com.baidu.common.cache.disc;

import com.baidu.common.cache.disc.naming.FileNameGenerator;
import com.baidu.common.cache.disc.naming.HashCodeFileNameGenerator;

import java.io.File;

/**
 * Base disc cache. Implements common functionality for disc cache.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see DiscCacheAware
 * @see FileNameGenerator
 */
public abstract class BaseDiscCache implements DiscCacheAware {

    private File cacheDir;

    private FileNameGenerator fileNameGenerator;

    public BaseDiscCache(File cacheDir) {
        this(cacheDir, new HashCodeFileNameGenerator());
    }

    public BaseDiscCache(File cacheDir, FileNameGenerator fileNameGenerator) {
        this.cacheDir = cacheDir;
        this.fileNameGenerator = fileNameGenerator;
    }

    @Override
    public File get(String key) {
        String fileName = fileNameGenerator.generate(key);
        return new File(cacheDir, fileName);
    }

    @Override
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    protected File getCacheDir() {
        return cacheDir;
    }
}
