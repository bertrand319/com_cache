package com.baidu.common.cache.core;

import com.baidu.common.ComInterface;
import com.baidu.common.cache.core.CacheComponentImpl.DeletePolicy;
import com.baidu.common.cache.core.CacheComponentImpl.ICacheCallBack;

/**
 *
 *
 * @date 2012-12-18
 * @version 1.0
 * @author huangweigan
 */
public interface ICacheComponent extends ComInterface {
	
	public boolean addCachePath(String path, DeletePolicy policy, Object value);
	
	public boolean deleteCachePath(String path);
	
	public boolean clearCache();
	
	public void putString(final String path, final String key, final String value);
	
	public void getString(final String path, final String key);
	
	public boolean putStringSync(final String path, final String key, final String value);
	
	public String getStringSync(final String path, final String key);
	
	public void setCallBackListner(ICacheCallBack cacheCallBack);
	
}

