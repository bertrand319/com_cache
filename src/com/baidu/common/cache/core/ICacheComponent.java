
package com.baidu.common.cache.core;

import android.graphics.Bitmap;

import com.baidu.common.ComInterface;
import com.baidu.common.cache.core.CacheComponentImpl.DiskPolicy;
import com.baidu.common.cache.core.CacheComponentImpl.MemoryPolicy;

/**
 * 缓存组件接口类
 * 
 * @date 2012-12-18
 * @version 1.0
 * @author huangweigan
 */
public interface ICacheComponent extends ComInterface {

    public boolean addDiskCachePath(String path, DiskPolicy policy, Object value);

    public boolean addMemoryCache(String path, MemoryPolicy policy, Object value);

    public boolean addCachePath(String path, DiskPolicy diskPolicy, Object diskValue,
            MemoryPolicy memoryPolicy, Object memoryValue);

    public boolean deleteCachePath(String path);

    public boolean clearCache();

    public void put(String path, String key, Object value);

    public void get(String path, String key);

    public void get(String path, String key, boolean isBitmap);

    public boolean putSync(String path, String key, Object value);

    public Object getSync(String path, String key);

    public Object getSync(String path, String key, boolean isBitmap);

    public void setCallBackListner(ICacheCallBack cacheCallBack);

}
