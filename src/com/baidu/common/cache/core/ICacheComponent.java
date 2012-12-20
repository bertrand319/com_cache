package com.baidu.common.cache.core;

import java.io.InputStream;
import java.io.OutputStream;

import com.baidu.common.ComInterface;
import com.baidu.common.cache.core.CacheComponentImpl.DeletePolicy;
import com.baidu.common.cache.core.ICacheCallBack;

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
	
	public void putString( String path,  String key,  String value);
	
	public void getString( String path,  String key);
	
	public boolean putStringSync( String path, String key,  String value);
	
	public String getStringSync(String path, String key);
	
	public void put(String path, String key, InputStream value);
	
	public void get(String path, String key);
	
	public boolean putSync(String path, String key, InputStream value);
	
	public InputStream getSync(String path, String key);
	
	public void setCallBackListner(ICacheCallBack cacheCallBack);
	
}

