package com.baidu.common.cache.core;

import java.util.Map;

import android.content.Context;

import com.baidu.common.ComFactory;
import com.baidu.common.ComInterface;
import com.baidu.common.cache.core.CacheComponentImpl.DeletePolicy;

/**
 *
 *
 * @date 2012-12-18
 * @version 1.0
 * @author huangweigan
 */
public class CacheComponentFactory extends ComFactory {
	
    public static ComInterface createInterface(Context context)
    {
        return createInterface(context, null);
    }
    
    public static ComInterface createInterface(Context context, Map<String, DeletePolicy> cachePaths)
    {
    	if (context != null)
        {
            CacheComponentImpl instance = new CacheComponentImpl(context, cachePaths);
            return instance.getInstance();
        }
        else
        {
            return null;
        }
    }

}
