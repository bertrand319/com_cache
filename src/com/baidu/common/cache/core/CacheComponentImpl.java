package com.baidu.common.cache.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.content.Context;

import com.baidu.common.ComInterface;
import com.baidu.common.cache.disc.BaseDiscCache;
import com.baidu.common.cache.disc.impl.FileCountLimitedDiscCache;
import com.baidu.common.cache.disc.impl.LimitedAgeDiscCache;
import com.baidu.common.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.baidu.common.cache.memory.impl.FIFOLimitedMemoryCache;
import com.baidu.common.cache.memory.impl.WeakMemoryCache;
import com.baidu.common.cache.memory2.BaseMemoryCache;
import com.baidu.common.cache.utils.FileUtils;

/**
 * 缓存组件实现类
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
		FIFO, WEAK_REFERENCE;
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
		if(path == null) return false;
		File cachePath = new File(path);
		if(!createCacheDir(cachePath)) return false;
		if(policy == null) policy = DiskPolicy.FILE_COUNT;
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
		if(mark == null) return false;
		if(policy == null) policy = MemoryPolicy.FIFO;
		BaseMemoryCache<String, Object> baseMemoryCache = null;
		if(policy == MemoryPolicy.FIFO)
		{
			baseMemoryCache = new FIFOLimitedMemoryCache(
					value == null ? FIFOLimitedMemoryCache.DEFAULT_SIZE_LIMIT : (Integer) value);
		}
		else if(policy == MemoryPolicy.WEAK_REFERENCE)
		{
			baseMemoryCache = new WeakMemoryCache();
		}
		if(baseMemoryCache != null)
		{
			mMemoryCaches.put(mark, baseMemoryCache);
		}
		return true;
	}

	@Override
	public boolean addCachePath(String path, DiskPolicy diskPolicy,
			Object diskValue, MemoryPolicy memoryPolicy, Object memoryValue) {
		// TODO Auto-generated method stub
		if(!addDiskCachePath(path, diskPolicy, diskValue)
				|| !addMemoryCache(path, memoryPolicy, memoryValue))	
		{
			return false;
		}
		return true;
	}
	
	private boolean createCacheDir(File path)
	{
		if(!path.exists())
		{
			if(!path.mkdirs())
			{
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
				while(t.hasNext())
				{
					String path = t.next();
					BaseDiscCache baseDiscCache = mDiskCaches.get(path);
					baseDiscCache.clear();
				}
				t = mMemoryCaches.keySet().iterator();
				while(t.hasNext())
				{
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

	private boolean putToDisk(final String path, final String key, final InputStream value) {
		// TODO Auto-generated method stub
		boolean flag = false;
		FileOutputStream out = null;
		try {
			BaseDiscCache baseDiscCache = mDiskCaches.get(path);
			File targetFile = baseDiscCache.get(key);
			out = new FileOutputStream(targetFile);
			FileUtils.copyStream(value, out);
			out.flush();
			baseDiscCache.put(key, targetFile);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	private InputStream getFromDisk(String path, String key) {
		// TODO Auto-generated method stub
		FileInputStream fis = null;
		try {
			BaseDiscCache baseDiscCache = mDiskCaches.get(path);
			File targetFile = baseDiscCache.get(key);
			fis = new FileInputStream(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			if(fis != null)
			{
				try {
					fis.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} 
		return fis;
	}
	
	private boolean putToMemory(String path, String key, Object value)
	{
		BaseMemoryCache<String, Object> baseMemoryCache = mMemoryCaches.get(path);
		if(baseMemoryCache == null) return false;
		baseMemoryCache.put(key, value);
		return true;
	}
	
	private Object getFromMemory(String path, String key)
	{
		Object obj = null;
		BaseMemoryCache<String, Object> baseMemoryCache = mMemoryCaches.get(path);
		if(baseMemoryCache != null) obj = baseMemoryCache.get(key);
		return obj;
	}


	@Override
	public void put(final String path, 
			final String key, 
			final Object value,
			final ITypeConvert typeConvert) {
		// TODO Auto-generated method stub
		mPutExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean flag = false;
				if(putToMemory(path, key, value))
				{
					flag = true;
				}
				if(typeConvert != null)
				{
					if(putToDisk(path, key, typeConvert.convertObjectToInputStream(value)))
					{
						flag = true;
					}
				}
				
				if(flag)
				{
					makeCallBack(MSG_PUT_SUCCESS, null);
				}
				else 
				{
					makeCallBack(MSG_GET_FAIL, null);
				}
			}
		});
	}

	@Override
	public void get(final String path, final String key, final ITypeConvert typeConvert) {
		// TODO Auto-generated method stub
		mGetExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean flag = false;
				Object res = getFromMemory(path, key);
				if(res != null)
				{
					flag = true;
				}
				
				if(!flag)
				{
					InputStream is = getFromDisk(path, key);
					if(is != null)
					{
						res = typeConvert.convertInputStreamToObject(is);
						if(res != null)
						{
							flag = true;
						}
					}
				}
				
				if(flag)
				{
					makeCallBack(MSG_GET_SUCCESS, res);
				}
				else 
				{
					makeCallBack(MSG_GET_FAIL, null);
				}
			}
		});
	}

	@Override
	public boolean putSync(String path, 
			String key, 
			Object value, 
			ITypeConvert typeConvert) {
		// TODO Auto-generated method stub
		boolean flag = false;
		if(putToMemory(path, key, value))
		{
			flag = true;
		}
		if(typeConvert != null)
		{
			if(putToDisk(path, key, typeConvert.convertObjectToInputStream(value)))
			{
				flag = true;
			}
		}
		return flag;
	}

	@Override
	public Object getSync(String path, String key, ITypeConvert typeConvert) {
		// TODO Auto-generated method stub
		boolean flag = false;
		Object res = getFromMemory(path, key);
		if(res != null)
		{
			flag = true;
		}
		
		if(!flag)
		{
			InputStream is = getFromDisk(path, key);
			if(is != null)
			{
				res = typeConvert.convertInputStreamToObject(is);
				if(res != null)
				{
					flag = true;
				}
			}
		}
		return flag;
	}

	@Override
	public ITypeConvert getStringTypeConvertInterface() {
		// TODO Auto-generated method stub
		return new StringTypeConvert();
	}

	@Override
	public ITypeConvert getBitmapTypeConvertInterface() {
		// TODO Auto-generated method stub
		return new BitmapTypeConvert();
	}
	
}
