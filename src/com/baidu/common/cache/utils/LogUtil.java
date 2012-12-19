
package com.baidu.common.cache.utils;

import android.util.Log;

/**
 * 日志管理类
 * 
 * @author yuankai
 * @version 1.0
 * @date 2012-11-18, modified by huangweigan
 */
public class LogUtil
{
    /**
     * 默认的文库日志Tag标签
     */
    public final static String DEFAULT_TAG = "cache";

    /**
     * 此常量用于控制是否打日志到Logcat中 release版本中本变量应置为false
     */
    private final static boolean LOG_ENABLE = true;

    /**
     * 打印debug级别的log
     * 
     * @param tag tag标签
     * @param str 内容
     */
    public static void d(String tag, Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.d(tag, o.toString());
        }
    }

    /**
     * 打印debug级别的log
     * 
     * @param str 内容
     */
    public static void d(Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.d(DEFAULT_TAG, o.toString());
        }
    }

    /**
     * 打印warning级别的log
     * 
     * @param tag tag标签
     * @param str 内容
     */
    public static void w(String tag, Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.w(tag, o.toString());
        }
    }

    /**
     * 打印warning级别的log
     * 
     * @param str 内容
     */
    public static void w(Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.w(DEFAULT_TAG, o.toString());
        }
    }

    /**
     * 打印error级别的log
     * 
     * @param tag tag标签
     * @param str 内容
     */
    public static void e(String tag, Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.e(tag, o.toString());
        }
    }

    /**
     * 打印error级别的log
     * 
     * @param str 内容
     */
    public static void e(Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.e(DEFAULT_TAG, o.toString());
        }
    }

    /**
     * 打印info级别的log
     * 
     * @param tag tag标签
     * @param str 内容
     */
    public static void i(String tag, Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.i(tag, o.toString());
        }
    }

    /**
     * 打印info级别的log
     * 
     * @param str 内容
     */
    public static void i(Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.i(DEFAULT_TAG, o.toString());
        }
    }

    /**
     * 打印verbose级别的log
     * 
     * @param tag tag标签
     * @param str 内容
     */
    public static void v(String tag, Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.v(tag, o.toString());
        }
    }

    /**
     * 打印verbose级别的log
     * 
     * @param str 内容
     */
    public static void v(Object o)
    {
        if (LOG_ENABLE && o != null)
        {
            Log.v(DEFAULT_TAG, o.toString());
        }
    }

}
