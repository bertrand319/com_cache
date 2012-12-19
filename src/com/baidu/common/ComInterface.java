
package com.baidu.common;

/**
 * @author yuankai
 * @version 1.0
 * @data 2012-7-9
 */
public interface ComInterface
{
    /**
     * 释放组件资源
     */
    public void release();

    /**
     * 获取组件接口对象
     * 
     * @return 对象
     */
    public ComInterface getInstance();
}
