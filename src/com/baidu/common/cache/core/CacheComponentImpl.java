
package com.baidu.common.cache.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baidu.common.ComInterface;
import com.baidu.common.cache.disc.BaseDiscCache;
import com.baidu.common.cache.disc.impl.FileCountLimitedDiscCache;
import com.baidu.common.cache.disc.impl.LimitedAgeDiscCache;
import com.baidu.common.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.baidu.common.cache.memory.BaseMemoryCache;
import com.baidu.common.cache.memory.impl.FIFOLimitedMemoryCache;
import com.baidu.common.cache.memory.impl.WeakMemoryCache;
import com.baidu.common.cache.utils.FileUtils;
import com.baidu.common.cache.utils.TimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 缂撳瓨缁勪欢瀹炵幇绫� *
 * 
 * @date 2012-12-18
 * @version 1.0
 * @author huangweigan
 */
public class CacheComponentImpl implements ICacheComponent {

    public static final int MSG_PUT_SUCCESS = 0;
    public static final int MSG_PUT_FAIL = 1;
    public static final int MSG_GET_SUCCESS = 2;
    public static final int MSG_GET_FAIL = 3;
    public static final int MSG_PUT_DISK_SUCCESS = 10;
    public static final int MSG_PUT_DISK_FAIL = 11;
    public static final int MSG_GET_DISK_SUCCESS = 12;
    public static final int MSG_GET_DISK_FAIL = 13;
    public static final int MSG_PUT_MEMORY_SUCCESS = 20;
    public static final int MSG_PUT_MEMORY_FAIL = 21;
    public static final int MSG_GET_MEMORY_SUCCESS = 22;
    public static final int MSG_GET_MEMORY_FAIL = 23;

    private Object mSync = new Object();
    private static volatile int sRef;
    private CacheComponentImpl mInstanse;
    private Map<String, BaseDiscCache> mDiskCaches = Collections
            .synchronizedMap(new HashMap<String, BaseDiscCache>());
    private Map<String, BaseMemoryCache<String, Object>> mMemoryCaches = Collections
            .synchronizedMap(new HashMap<String, BaseMemoryCache<String, Object>>());
    private ExecutorService mPutExecutor;
    private ExecutorService mGetExecutor;
    private ICacheCallBack mICacheCallBack;

    public enum DiskPolicy {
        FILE_COUNT, LIMITED_AGE, TOTAL_SIZE, UNLIMITED;
    }

    public enum MemoryPolicy {
        FIFO, LRU, WEAK_REFERENCE;
    }

    CacheComponentImpl(Context context) {
        // TODO Auto-generated constflagructor stub
        synchronized (mSync) {
            setupThreadPool();
            if (mInstanse == null) {
                mInstanse = this;
            }
        }
        sRef++;

    }

    private void setupThreadPool() {
        mPutExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            }
        });
        mGetExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                // TODO Auto-generated method stub
                Thread t = new Thread(r);
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        });
    }

    @Override
    public void release() {
        // TODO Auto-generated method stub
        if (sRef == 0) {
            mInstanse = null;
            mDiskCaches = null;
        } else {
            sRef--;
        }
    }

    @Override
    public ComInterface getInstance() {
        // TODO Auto-generated method stub
        return mInstanse;
    }

    @Override
    public boolean addDiskCachePath(String path, DiskPolicy policy, Object value) {
        // TODO Auto-generated method stub
        if (path == null)
            return false;
        File cachePath = new File(path);
        if (!createCacheDir(cachePath))
            return false;
        if (policy == null)
            policy = DiskPolicy.FILE_COUNT;
        BaseDiscCache baseDiscCache = null;
        if (policy == DiskPolicy.FILE_COUNT) {
            baseDiscCache = new FileCountLimitedDiscCache(cachePath,
                    value == null ? FileCountLimitedDiscCache.DEFAULT_FILE_COUNT_NUM
                            : (Integer) value);
        } else if (policy == DiskPolicy.LIMITED_AGE) {
            baseDiscCache = new LimitedAgeDiscCache(cachePath,
                    value == null ? LimitedAgeDiscCache.DEFAULT_MAX_AGE
                            : (Long) value);
        } else if (policy == DiskPolicy.TOTAL_SIZE) {
            baseDiscCache = new TotalSizeLimitedDiscCache(cachePath,
                    value == null ? TotalSizeLimitedDiscCache.DEFAUL_MAX_SIZE
                            : (Integer) value);
        }
        if (baseDiscCache != null)
            mDiskCaches.put(path, baseDiscCache);
        return true;
    }

    @Override
    public boolean addMemoryCache(String mark, MemoryPolicy policy, Object value) {
        // TODO Auto-generated method stub
        if (mark == null)
            return false;
        if (policy == null)
            policy = MemoryPolicy.FIFO;
        BaseMemoryCache<String, Object> baseMemoryCache = null;
        if (policy == MemoryPolicy.FIFO) {
            baseMemoryCache = new FIFOLimitedMemoryCache(
                    value == null ? FIFOLimitedMemoryCache.DEFAULT_SIZE_LIMIT : (Integer) value);
        }
        else if (policy == MemoryPolicy.WEAK_REFERENCE) {
            baseMemoryCache = new WeakMemoryCache();
        }
        if (baseMemoryCache != null) {
            mMemoryCaches.put(mark, baseMemoryCache);
        }
        return true;
    }

    @Override
    public boolean addCachePath(String path, DiskPolicy diskPolicy,
            Object diskValue, MemoryPolicy memoryPolicy, Object memoryValue) {
        // TODO Auto-generated method stub
        if (!addDiskCachePath(path, diskPolicy, diskValue)
                || !addMemoryCache(path, memoryPolicy, memoryValue)) {
            return false;
        }
        return true;
    }

    private boolean createCacheDir(File path)
    {
        if (!path.exists()) {
            if (!path.mkdirs()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean deleteCachePath(String path) {
        // TODO Auto-generated method stub
        mMemoryCaches.remove(path);
        mDiskCaches.remove(path);
        return true;
    }

    @Override
    public boolean clearCache() {
        // TODO Auto-generated method stub
        mPutExecutor.submit(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Iterator<String> t = mDiskCaches.keySet().iterator();
                while (t.hasNext()) {
                    String path = t.next();
                    BaseDiscCache baseDiscCache = mDiskCaches.get(path);
                    baseDiscCache.clear();
                }
                t = mMemoryCaches.keySet().iterator();
                while (t.hasNext()) {
                    String path = t.next();
                    BaseMemoryCache<String, Object> baseMemoryCache = mMemoryCaches.get(path);
                    baseMemoryCache.clear();
                }
            }
        });
        return false;
    }

    @Override
    public void setCallBackListner(ICacheCallBack cacheCallBack) {
        // TODO Auto-generated method stub
        mICacheCallBack = cacheCallBack;
    }

    private void makeCallBack(int errorNo, Object obj) {
        if (mICacheCallBack != null) {
            mICacheCallBack.onCallBack(errorNo, obj);
        }
    }

    private boolean putToDisk(final String path, final String key, Object obj) {
        // TODO Auto-generated method stub
        boolean flag = false;
        ObjectOutputStream oos = null;
        try {
            BaseDiscCache baseDiscCache = mDiskCaches.get(path);
            if (baseDiscCache != null) {
                File targetFile = baseDiscCache.get(key);
                oos = new ObjectOutputStream(new FileOutputStream(targetFile));
                oos.writeObject(obj);
                oos.flush();
                baseDiscCache.put(key, targetFile);
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return flag;
    }

    private Object getFromDisk(String path, String key) {
        // TODO Auto-generated method stub
        ObjectInputStream ois = null;
        Object res = null;
        try {
            BaseDiscCache baseDiscCache = mDiskCaches.get(path);
            if (baseDiscCache != null) {
                File targetFile = baseDiscCache.get(key);
                ois = new ObjectInputStream(new FileInputStream(targetFile));
                res = ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        return res;
    }

    private boolean putBitmapToDisk(final String path, final String key, Bitmap bm)
    {
        boolean flag = false;
        FileOutputStream fos = null;
        try {
            BaseDiscCache baseDiscCache = mDiskCaches.get(path);
            if (baseDiscCache != null) {
                File targetFile = baseDiscCache.get(key);
                fos = new FileOutputStream(targetFile);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                flag = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            flag = false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    private Bitmap getBitmapFromDisk(final String path, final String key)
    {
        Bitmap bm = null;
        try {
            BaseDiscCache baseDiscCache = mDiskCaches.get(path);
            if (baseDiscCache != null) {
                File targetFile = baseDiscCache.get(key);
                bm = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    private boolean putToMemory(String path, String key, Object value)
    {
        BaseMemoryCache<String, Object> baseMemoryCache = mMemoryCaches.get(path);
        if (baseMemoryCache == null) return false;
        return baseMemoryCache.put(key, value);
    }

    private Object getFromMemory(String path, String key)
    {
        Object obj = null;
        BaseMemoryCache<String, Object> baseMemoryCache = mMemoryCaches.get(path);
        if (baseMemoryCache != null)
            obj = baseMemoryCache.get(key);
        return obj;
    }

    @Override
    public void put(final String path,
            final String key,
            final Object value) {
        // TODO Auto-generated method stub
        put(path, key, value, false);
    }
    
    
    @Override
    public void put(final String path, final String key, final Object value, final boolean isBitmap) {
        // TODO Auto-generated method stub
        mPutExecutor.submit(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                TimeUtils.begin();
                boolean flag = false;
                if (putToMemory(path, key, value))
                    flag = true;
                if (isBitmap) {
                    if (putBitmapToDisk(path, key, (Bitmap) value)) flag = true;
                }
                else {
                    if (putToDisk(path, key, value))    flag = true;
                }
                TimeUtils.end();
                if (flag) {
                    makeCallBack(MSG_PUT_SUCCESS, null);
                }
                else {
                    makeCallBack(MSG_PUT_FAIL, null);
                }
            }
        });
    }

    @Override
    public void get(final String path, final String key) {
        // TODO Auto-generated method stub
        get(path, key, false);
    }

    @Override
    public boolean putSync(String path,
            String key,
            Object value) {
        // TODO Auto-generated method stub
        return putSync(path, key, value, false);
    }

    @Override
    public boolean putSync(String path, String key, Object value, boolean isBitmap) {
        // TODO Auto-generated method stub
        boolean flag = false;
        if (putToMemory(path, key, value)) {
            flag = true;
        }

        if (isBitmap) {
            if (putBitmapToDisk(path, key, (Bitmap) value))
                flag = true;
        }
        else {
            if (putToDisk(path, key, value))
                flag = true;
        }
        return flag;
    }

    @Override
    public Object getSync(String path, String key) {
        // TODO Auto-generated method stub
        return getSync(path, key, false);
    }

    @Override
    public void get(final String path, final String key, final boolean isBitmap) {
        // TODO Auto-generated method stub
        mGetExecutor.submit(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                
                TimeUtils.begin();
                boolean flag = false;
                Object res = getFromMemory(path, key);
                if (res != null) {
                    flag = true;
                }

                if (!flag) {
                    if (isBitmap) {
                        res = getBitmapFromDisk(path, key);
                    }
                    else {
                        res = getFromDisk(path, key);
                    }
                    if (res != null)
                        flag = true;
                }
                
                TimeUtils.end();

                if (flag) {
                    makeCallBack(MSG_GET_SUCCESS, res);
                }
                else {
                    makeCallBack(MSG_GET_FAIL, null);
                }
            }
        });
    }

    @Override
    public Object getSync(String path, String key, boolean isBitmap) {
        // TODO Auto-generated method stub
        boolean flag = false;
        Object res = getFromMemory(path, key);
        if (res != null) {
            flag = true;
        }

        if (!flag) {
            if (isBitmap) {
                res = getBitmapFromDisk(path, key);
            }
            else {
                res = getFromDisk(path, key);
            }
            if (res != null)
                flag = true;
        }
        return res;
    }

    
}
