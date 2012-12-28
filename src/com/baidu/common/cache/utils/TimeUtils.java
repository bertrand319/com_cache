package com.baidu.common.cache.utils;

/**
 * Time Utilities
 *
 * @author huangweigan
 * @date 2012-12-28
 */
public class TimeUtils {
    
    private static final String TAG = TimeUtils.class.getName();
    
    private static long mBegintime;
    
    private static long mEndTime;
    
    public static void begin()
    {
        mBegintime = System.currentTimeMillis();
    }
    
    public static void end()
    {
        mEndTime = System.currentTimeMillis();
        LogUtil.d(TAG, mEndTime - mBegintime);
    }

}
