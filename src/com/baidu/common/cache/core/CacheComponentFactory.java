
package com.baidu.common.cache.core;

import android.content.Context;

import com.baidu.common.ComFactory;
import com.baidu.common.ComInterface;

/**
 * @date 2012-12-18
 * @version 1.0
 * @author huangweigan
 */
public class CacheComponentFactory extends ComFactory {

    public static ComInterface createInterface(Context context) {
        if (context != null) {
            CacheComponentImpl instance = new CacheComponentImpl(context);
            return instance.getInstance();
        }
        else {
            return null;
        }
    }
}
