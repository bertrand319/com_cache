package com.baidu.common.cache.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.baidu.common.cache.utils.FileUtils;

/**
 * 
 * 
 * @date 2012-12-18
 * @version 1.0
 * @author huangweigan
 */
public class CacheComponentImpl implements ICacheComponent {

	public static final int MSG_SUCCES = 0;
	public static final int MSG_FAIL = 1;

	private Object mSync = new Object();
	private static volatile int sRef;
	private CacheComponentImpl mInstanse;
	private Map<String, BaseDiscCache> mCaches = Collections
			.synchronizedMap(new HashMap<String, BaseDiscCache>());
	private ExecutorService mExecutor;
	private ICacheCallBack mICacheCallBack;

	public enum DeletePolicy {
		FILE_COUNT, LIMITED_AGE, TOTAL_SIZE, UNLIMITED;
	}

	CacheComponentImpl(Context context) {
		// TODO Auto-generated constflagructor stub
		this(context, null);

	}

	CacheComponentImpl(Context context, Map<String, DeletePolicy> cachePath) {
		// TODO Auto-generated constructor stub

		synchronized (mSync) {
			setupThreadPool();
			if (cachePath != null) {
				Iterator<String> t = cachePath.keySet().iterator();
				while (t.hasNext()) {
					String path = t.next();
					DeletePolicy policy = cachePath.get(path);
					addCachePath(path, policy, null);
				}
			}
			if (mInstanse == null) {
				mInstanse = this;
			}
		}
		sRef++;
	}

	private void setupThreadPool() {
		mExecutor = Executors
				.newSingleThreadExecutor(new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setPriority(Thread.MIN_PRIORITY);
						return t;
					}
				});
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		if (sRef == 0) {
			mInstanse = null;
			mCaches = null;
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
	public boolean addCachePath(String path, DeletePolicy policy, Object value) {
		// TODO Auto-generated method stub
		File cachePath = new File(path);
		if(!createCacheDir(cachePath))
			return false;
		BaseDiscCache baseDiscCache = null;
		if (policy == DeletePolicy.FILE_COUNT) {
			baseDiscCache = new FileCountLimitedDiscCache(cachePath,
					value == null ? FileCountLimitedDiscCache.DEFAULT_FILE_COUNT_NUM
							: (Integer) value);
		} else if (policy == DeletePolicy.LIMITED_AGE) {
			baseDiscCache = new LimitedAgeDiscCache(cachePath,
					value == null ? LimitedAgeDiscCache.DEFAULT_MAX_AGE
							: (Long) value);
		} else if (policy == DeletePolicy.TOTAL_SIZE) {
			baseDiscCache = new TotalSizeLimitedDiscCache(cachePath,
					value == null ? TotalSizeLimitedDiscCache.DEFAUL_MAX_SIZE
							: (Integer) value);
		}
		if (baseDiscCache != null)
			mCaches.put(path, baseDiscCache);
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
		mCaches.remove(path);
		return true;
	}

	@Override
	public boolean clearCache() {
		// TODO Auto-generated method stub
		mExecutor.submit(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Iterator<String> t = mCaches.keySet().iterator();
				while(t.hasNext())
				{
					String path = t.next();
					BaseDiscCache baseDiscCache = mCaches.get(path);
					baseDiscCache.clear();
				}
			}
		});
		return false;
	}

	@Override
	public void setCallBackListner(ICacheCallBack cacheCallBack) {
		// TODO Auto-generated method stub

	}

	@Override
	public void putString(final String path, final String key,
			final String value) {
		// TODO Auto-generated method stub
		mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PrintWriter out = null;
				try {
					BaseDiscCache baseDiscCache = mCaches.get(path);
					File targetFile = baseDiscCache.get(key);
					out = new PrintWriter(new FileOutputStream(targetFile));
					out.println(value);
					out.flush();
					baseDiscCache.put(key, targetFile);
					makeCallBack(MSG_SUCCES, null);
				} catch (Exception e) {
					e.printStackTrace();
					makeCallBack(MSG_FAIL, e);
				} finally {
					if (out != null) {
						out.close();
					}
				}

			}
		});
	}

	@Override
	public void getString(final String path, final String key) {
		// TODO Auto-generated method stub
		mExecutor.submit(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String value = "";
				BufferedReader br = null;
				// 创建新的BufferedReader对象
				try {
					BaseDiscCache baseDiscCache = mCaches.get(path);
					File targetFile = baseDiscCache.get(key);
					br = new BufferedReader(new FileReader(targetFile));
					String tempString = null;
					// readLine()读取每行数据，如果没有值则为null(read读取每一个字符转换为char类型)
					while ((tempString = br.readLine()) != null) {
						value += tempString;
					}
					mICacheCallBack.onCallBack(MSG_SUCCES, value);
				} catch (Exception e) {
					e.printStackTrace();
					mICacheCallBack.onCallBack(MSG_FAIL, null);
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			}
		});
	}
	

	@Override
	public boolean putStringSync(String path, String key, String value) {
		// TODO Auto-generated method stub
		boolean flag = false;
		PrintWriter out = null;
		try {
			BaseDiscCache baseDiscCache = mCaches.get(path);
			File targetFile = baseDiscCache.get(key);
			baseDiscCache.put(key, targetFile);
			out = new PrintWriter(new FileOutputStream(targetFile));
			out.println(value);
			baseDiscCache.put(key, targetFile);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return flag;
	}

	@Override
	public String getStringSync(String path, String key) {
		// TODO Auto-generated method stub
		String value = "";
		BufferedReader br = null;
		// 创建新的BufferedReader对象
		try {
			BaseDiscCache baseDiscCache = mCaches.get(path);
			File targetFile = baseDiscCache.get(key);
			br = new BufferedReader(new FileReader(targetFile));
			String tempString = null;
			// readLine()读取每行数据，如果没有值则为null(read读取每一个字符转换为char类型)
			while ((tempString = br.readLine()) != null) {
				value += tempString;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}

	private void makeCallBack(int errorNo, Object obj) {
		if (mICacheCallBack != null) {
			mICacheCallBack.onCallBack(errorNo, obj);
		}
	}

	public interface ICacheCallBack {
		public void onCallBack(int errorNo, Object obj);
	}


}
